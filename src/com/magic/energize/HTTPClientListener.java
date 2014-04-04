package com.magic.energize;

public interface HTTPClientListener {
	void onRequestCompleted(String method, String result);
}