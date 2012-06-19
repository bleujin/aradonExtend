package net.ion.im.bbs.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionResponse {
	private boolean isSuccess = true;
	private String resultMessage;
	
	public ActionResponse() {
		
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public List<Map<String, ?>> toRepresentaion() {
		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", isSuccess);
		resultMap.put("message", resultMessage);
		
		result.add(resultMap);
		
		return result;
	}
}
