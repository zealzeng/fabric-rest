package com.whlylc.fabricrest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.whlylc.fabricrest.vo.Result;
import com.whlylc.server.http.HttpRequest;
import com.whlylc.server.http.HttpResponse;
import com.whlylc.server.http.HttpService;

/**
 * Created by Zeal on 2018/11/13 0013.
 */
public abstract class CmnJsonService extends HttpService {

    /**
     * Request body to json object
     * @param request
     * @return
     */
    protected JSONObject getRequestJsonObject(HttpRequest request) {
        return JSON.parseObject(request.getRequestBody(), Feature.OrderedField);
    }

    /**
     * Request body to json array
     * @param request
     * @return
     */
    protected JSONArray getRequestJsonArray(HttpRequest request) {
        return JSON.parseArray(request.getRequestBody());
    }
    
    /**
     * Output json error
     * @param response
     * @param resultCode
     */
    protected void jsonResult(HttpResponse response, int resultCode) {
        //FIXME Get result message from result code
        jsonResult(response, resultCode, "", null);
    }

    protected void jsonResult(HttpResponse response, int resultCode, Object resultEntity) {
        //FIXME Get result message from result code
        jsonResult(response, resultCode, "", resultEntity);
    }


    /**
     * Output json result
     * @param response
     * @param resultCode
     * @param resultMessage
     * @param resultEntity
     */
    protected void jsonResult(HttpResponse response, int resultCode, String resultMessage, Object resultEntity) {
        Result<Object> result = new Result<>();
        result.setResultCode(resultCode);
        if (resultMessage != null) {
            result.setResultMessage(resultMessage);
        }
        result.setResultMessage(resultMessage);
        if (resultEntity != null) {
            result.setResultEntity(resultEntity);
        }
        String json = JSON.toJSONString(result, SerializerFeature.SortField);
        response.write(json);
    }

    /**
     * Output json result
     * @param response
     * @param result
     */
    protected void jsonResult(HttpResponse response, Result<?> result) {
        String json = JSON.toJSONString(result, SerializerFeature.SortField);
        response.write(json);
    }
}
