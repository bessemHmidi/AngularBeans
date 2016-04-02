/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors. Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

public class ResponseReceiver extends GenericReceiver {

	public ResponseReceiver(SockJsRequest request, SockJsResponse response, SockJsServer.Options options) {
		this.request = request;
		this.response = response;
		this.options = options;
		currResponseSize = 0;
		maxResponseSize = options.responseLimit;
	}

	@Override
	public boolean doSendFrame(String payload) {
		return doSendFrame(payload, true);
	}

	private boolean doSendFrame(String payload, boolean checkSize) {
		if (checkSize) {
			currResponseSize += payload.length();
		}
		boolean r = false;
		try {
			response.write(payload);
			r = true;
		} catch (SockJsException x) {
			didAbort();
			return r;
		}
		if (checkSize) {
			if (maxResponseSize >= 0 && currResponseSize >= maxResponseSize) {
				didClose();
			}
		}
		return r;
	}

	@Override
	public void checkAlive() {
		doSendFrame("h", false);
	}

	@Override
	protected void didClose() {
		super.didClose();
		try {
			response.end();
		} catch (Exception x) {//
		}
		response = null;
	}

	protected SockJsRequest request;
	protected SockJsResponse response;
	protected SockJsServer.Options options;
	protected int currResponseSize;
	protected int maxResponseSize = -1;
}
