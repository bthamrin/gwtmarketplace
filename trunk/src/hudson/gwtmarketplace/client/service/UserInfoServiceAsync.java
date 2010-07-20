package hudson.gwtmarketplace.client.service;

import hudson.gwtmarketplace.client.model.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	public void login(String loginCallbackUri, String logoutCallbackUri, AsyncCallback<UserInfo> callback);
}