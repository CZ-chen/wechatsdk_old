package com.tagsin.wechat_sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import com.tagsin.tutils.http.HttpResult;
import com.tagsin.tutils.http.Requester;
import com.tagsin.tutils.http.Requester.Method;
import com.tagsin.tutils.json.JsonUtils;
import com.tagsin.wechat_sdk.token.TokenType;
import com.tagsin.wechat_sdk.vo.CardVo;
import com.tagsin.wechat_sdk.vo.UserCardVo;

public class WxCardServerApi {
	
	private static final Logger LOGGER = Logger.getLogger(WxCardServerApi.class);
	
	public static final String CARD_STATUS_VERIFY_OK = "CARD_STATUS_VERIFY_OK";
	public static final String CARD_STATUS_EXPIRED = "CARD_STATUS_EXPIRED";
	public static final String CARD_STATUS_DISCARD = "CARD_STATUS_DISCARD";
	
	private static byte[] execute(App app,String url,Method method,Map<String, Object> reqData,boolean token) throws WXServerApiException{
		try {
			Requester req = Requester.builder().setMethod(method)
					.setUrl(url);
			if(token){
				req = req.addUrlParm("access_token", app.tokenManager.getToken(TokenType.ACCESS_TOKEN));
			}
			System.out.println("json:"+JsonUtils.toJson(reqData));		
			LOGGER.debug("json:"+JsonUtils.toJson(reqData));
			req.setBody(JsonUtils.toJson(reqData));
			HttpResult result = req.execute();
			if(result.getCode()==200){
				return result.getRespBody();
			}
			throw new WXServerApiException("failed execure url :"+req.getUrl()+"  with code: " + result.getCode());
		} catch (Exception e) {
			throw new WXServerApiException(e.getMessage(),e);
		}
	}
	
	private static byte[] executeByPost(App app,String url,Map<String, Object> reqData) throws WXServerApiException{
		return execute(app, url, Method.POST, reqData, true);
	}
	
	/**
	 * 添加卡券
	 * @param app
	 * @param cardInfoMap
	 * @return
	 */
	public static String createCard(App app,Map<String, Object> cardInfoMap) throws WXServerApiException{
		Map<String,Object> reqData = WXServerApi.convertPathValueToMap(cardInfoMap, "card");
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/create", reqData);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		if(errcode == 0){
			return objNode.get("card_id").getTextValue();
		}
		throw new WXServerApiException("fail createCard ,response result:"+objNode.toString());
	}
	
	/**
	 * 获取用户卡券
	 * @param app
	 * @param openId
	 * @return
	 */
	public static List<UserCardVo> getUserCardList(App app,String openId,String cardId) throws WXServerApiException{
			Map<String, Object> cardInfoMap = new HashMap<String, Object>();
			cardInfoMap.put("openid", openId);
			cardInfoMap.put("card_id", cardId);
			byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/user/getcardlist", cardInfoMap);
			ObjectNode objNode = JsonUtils.readJsonObject(respBody);
			int errcode = objNode.get("errcode").getIntValue();
			if(errcode == 0){
				List<UserCardVo> list = new ArrayList<UserCardVo>();
				JsonNode jsonNode = JsonUtils.readJsonObject(objNode.toString());
				ArrayNode arrayNode = (ArrayNode)jsonNode.get("card_list");
				for(JsonNode node : arrayNode){
					UserCardVo vo = new UserCardVo();
					vo.setCardId(node.get("card_id").asText());
					vo.setCode(node.get("code").asText());
					list.add(vo);
				}
				return list;
			}
			throw new WXServerApiException("fail getUserCardList ,response result: " + objNode.toString());
	}
	
	/**
	 * 批量查询卡券列表
	 * @param app
	 * @param cardInfoMap
	 * @return
	*/
	public static List<String> getCardList(App app,int offset,int count) throws WXServerApiException{
		Map<String, Object> reqData = new HashMap<String, Object>();
		reqData.put("offset", offset);
		reqData.put("count", count);
		reqData.put("status_list", new String[]{CARD_STATUS_VERIFY_OK});
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/batchget", reqData);
		
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		if(errcode == 0){
			return JsonUtils.fromJson(objNode.get("card_id_list").toString(),new TypeReference<List<String>>(){});
		}
		throw new WXServerApiException("fail getCardList ,response result: " + objNode.toString());
	}
	
	/**
	 * 导入code
	 * @param app
	 * @param cardId
	 * @param codes
	 * @return
	 */
	public static boolean depositCode(App app,String cardId,Set<String>codes) throws WXServerApiException{
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("card_id", cardId);
		reqData.put("code", codes);
		byte [] respBody = executeByPost(app, "http://api.weixin.qq.com/card/code/deposit", reqData);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		return errcode == 0;
	}
	
	/**
	 * 核查code
	 * @param app
	 * @param cardId
	 * @param codes
	 * @return
	 */
	public static boolean checkCode(App app,String cardId,Set<String>codes) throws WXServerApiException{
			Map<String,Object> reqData = new HashMap<String, Object>();
			reqData.put("card_id", cardId);
			reqData.put("code", codes);
			byte [] respBody = executeByPost(app, "http://api.weixin.qq.com/card/code/checkcode", reqData);
			ObjectNode objNode = JsonUtils.readJsonObject(respBody);
			LOGGER.debug("result:"+objNode.toString());
			int errcode = objNode.get("errcode").getIntValue();
			if(errcode == 0){
				if(((ArrayNode)objNode.get("exist_code")).size() == codes.size()){
					return true;
				}
			}
			throw new WXServerApiException("checkCode size: " + objNode.get("exists_code").size()+", input code size:"+codes.size());
	}
	
	/**
	 * 获取用户领取卡券
	 * @param app
	 * @param openId
	 * @param cardId
	 * @return
	 */
	public static String getCanConsumeCardCode(App app,String openId,String cardId) throws WXServerApiException{
		Map<String, Object> reqData = new HashMap<String, Object>();
		reqData.put("openid", openId);
		reqData.put("card_id", cardId);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/user/getcardlist", reqData);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		int errcode = objNode.get("errcode").getIntValue();
		if(errcode == 0){
			JsonNode jsonNode = JsonUtils.readJsonObject(objNode.toString());
			ArrayNode arrayNode = (ArrayNode)jsonNode.get("card_list");
			for(JsonNode node : arrayNode){
				String code = node.get("code").getTextValue();
				try {
					if(checkConsume(app, code, false)){
						return code;
					}
				} catch (Exception e) {
					
				}
				
			}
		}
		throw new WXServerApiException("fail getCanConsumeCardCode ,no canConsumeCardCode ");
	}
	
	/**
	 * 核销卡券
	 * @param app
	 * @param openId
	 * @param cardId
	 * @return
	 */
	public static String consumeCard(App app,String openId,String cardId) throws WXServerApiException{
		boolean flag = false;
		String code = getCanConsumeCardCode(app, openId, cardId);
		if(StringUtils.isNotBlank(code)){
			if(checkConsume(app, code, false)){
				flag = consumeCard(app, code);
				LOGGER.debug("核销flag："+flag+" ,code:"+code);
				if(flag){
					return code;
				}
			}
		}
		throw new WXServerApiException("fail consumeCard code is null");
	}
	
	/**
	 * 判断卡券是否可核销
	 * @param app
	 * @param cardInfoMap
	 * @return
	 */
	public static boolean checkConsume(App app,String code,boolean checkConsume) throws WXServerApiException{
		Map<String, Object> cardMap = new HashMap<String, Object>();
		cardMap.put("code", code);
		cardMap.put("check_consume", checkConsume);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/code/get", cardMap);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		if(errcode == 0){
			return objNode.get("can_consume").asBoolean();
		}
		throw new WXServerApiException("fail checkConsume ,response result: " + objNode.toString());
	}
	
	/**
	 * 核销卡券
	 * @param app
	 * @param cardInfoMap
	 * @return
	 */
	public static boolean consumeCard(App app,String code) throws WXServerApiException{
		Map<String, Object> cardMap = new HashMap<String, Object>();
		cardMap.put("code", code);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/code/consume", cardMap);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		return errcode == 0;
	}
	
	/**
	 * 修改库存
	 * @param app
	 * @param cardId
	 * @param increaseNum
	 * @return
	 */
	public static boolean modifyStock(App app,String cardId,long increaseNum) throws WXServerApiException{
		Map<String,Object> reqData = new HashMap<String, Object>();
		reqData.put("card_id", cardId);
		reqData.put("increase_stock_value", increaseNum);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/modifystock", reqData);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		return errcode == 0;
	}
	
	/**
	 * 获取卡券
	 * @param app
	 * @param cardInfoMap
	 * @return
	 */
	public static CardVo getCard(App app,String cardId){
		Map<String, Object> cardInfoMap = new HashMap<String, Object>();
		cardInfoMap.put("card_id", cardId);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/get", cardInfoMap);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		if(errcode == 0){
			CardVo cardVo = new CardVo();
			String cardType = objNode.get("card").get("card_type").getTextValue().toLowerCase();
			cardVo.setStatus(objNode.get("card").get(cardType).get("base_info").get("status").getTextValue());
			return cardVo;
		}
		throw new WXServerApiException("fail getCard ,response result: " + objNode.toString());
	}

	/**
	 * 删除卡券
	 * @param app
	 * @param cardId
	 * @return
	 */
	public static boolean delCard(App app,String cardId){
		Map<String, Object> cardInfoMap = new HashMap<String, Object>();
		cardInfoMap.put("card_id", cardId);
		byte [] respBody = executeByPost(app, "https://api.weixin.qq.com/card/delete", cardInfoMap);
		ObjectNode objNode = JsonUtils.readJsonObject(respBody);
		LOGGER.debug("result:"+objNode.toString());
		int errcode = objNode.get("errcode").getIntValue();
		return errcode == 0;
	}
	
}
