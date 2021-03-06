package com.vunke.videochat.tools;

import android.util.Log;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;
public   class AccountBuilder {
    	private LinphoneCore lc;
		private String tempUsername;
		private String tempDisplayName;
		private String tempUserId;
		private String tempPassword;
		private String tempHa1;
		private String tempDomain;
		private String tempProxy;
		private String tempRealm;
		private String tempPrefix;
		private boolean tempOutboundProxy;
		private String tempContactsParams;
		private String tempExpire;
		private LinphoneAddress.TransportType tempTransport;
		private boolean tempAvpfEnabled = false;
		private int tempAvpfRRInterval = 0;
		private String tempQualityReportingCollector;
		private boolean tempQualityReportingEnabled = false;
		private int tempQualityReportingInterval = 0;
		private boolean tempEnabled = true;
		private boolean tempNoDefault = false;


		public AccountBuilder(LinphoneCore lc) {
			this.lc = lc;
		}

		public AccountBuilder setTransport(LinphoneAddress.TransportType transport) {
			tempTransport = transport;
			return this;
		}

		public AccountBuilder setUsername(String username) {
			tempUsername = username;
			return this;
		}

		public AccountBuilder setDisplayName(String displayName) {
			tempDisplayName = displayName;
			return this;
		}

		public AccountBuilder setPassword(String password) {
			tempPassword = password;
			return this;
		}

		public AccountBuilder setHa1(String ha1) {
			tempHa1 = ha1;
			return this;
		}

		public AccountBuilder setDomain(String domain) {
			tempDomain = domain;
			return this;
		}

		public AccountBuilder setProxy(String proxy) {
			tempProxy = proxy;
			return this;
		}

		public AccountBuilder setOutboundProxyEnabled(boolean enabled) {
			tempOutboundProxy = enabled;
			return this;
		}

		public AccountBuilder setContactParameters(String contactParams) {
			tempContactsParams = contactParams;
			return this;
		}

		public AccountBuilder setExpires(String expire) {
			tempExpire = expire;
			return this;
		}

		public AccountBuilder setUserId(String userId) {
			tempUserId = userId;
			return this;
		}

		public AccountBuilder setAvpfEnabled(boolean enable) {
			tempAvpfEnabled = enable;
			return this;
		}

		public AccountBuilder setAvpfRRInterval(int interval) {
			tempAvpfRRInterval = interval;
			return this;
		}

		public AccountBuilder setRealm(String realm) {
			tempRealm = realm;
			return this;
		}

		public AccountBuilder setQualityReportingCollector(String collector) {
			tempQualityReportingCollector = collector;
			return this;
		}

		public AccountBuilder setPrefix(String prefix) {
			tempPrefix = prefix;
			return this;
		}

		public AccountBuilder setQualityReportingEnabled(boolean enable) {
			tempQualityReportingEnabled = enable;
			return this;
		}

		public AccountBuilder setQualityReportingInterval(int interval) {
			tempQualityReportingInterval = interval;
			return this;
		}

		public AccountBuilder setEnabled(boolean enable) {
			tempEnabled = enable;
			return this;
		}

		public AccountBuilder setNoDefault(boolean yesno) {
			tempNoDefault = yesno;
			return this;
		}

		/**
		 * Creates a new account
		 * @throws LinphoneCoreException
		 */
		public void saveNewAccount(LinphoneCoreFactory lcFactory) throws LinphoneCoreException {

			if (tempUsername == null || tempUsername.length() < 1 || tempDomain == null || tempDomain.length() < 1) {
				Log.i("提示","Skipping account save: username or domain not provided");
				return;
			}

//			String identity = "sip:" + tempUsername + "@" + tempDomain;
//			String identity = "sip:"+ tempUsername;
			String identity = "sip:" + tempUsername + "@hu.ctcims.cn";
			String proxy = "sip:";


			if (tempProxy == null) {
				proxy += tempDomain;
			} else {
				if (!tempProxy.startsWith("sip:") && !tempProxy.startsWith("<sip:")
						&& !tempProxy.startsWith("sips:") && !tempProxy.startsWith("<sips:")) {
					proxy += tempProxy;
				} else {
					proxy = tempProxy;
				}

			}
			Log.i("saveNewAccount", "saveNewAccount: proxy:"+proxy);
			LinphoneAddress proxyAddr = lcFactory.createLinphoneAddress(proxy);

			Log.i("saveNewAccount", "saveNewAccount: identity:"+identity);
			LinphoneAddress identityAddr = lcFactory.createLinphoneAddress(identity);

			if (tempDisplayName != null) {
				identityAddr.setDisplayName(tempDisplayName);
			}

			if (tempTransport != null) {
				proxyAddr.setTransport(tempTransport);
			}

			String route = tempOutboundProxy ? proxyAddr.asStringUriOnly() : null;


			LinphoneProxyConfig prxCfg = lc.createProxyConfig(identityAddr.asString(), proxyAddr.asStringUriOnly(), route, tempEnabled);

			if (tempContactsParams != null)
				prxCfg.setContactUriParameters(tempContactsParams);
			if (tempExpire != null) {
				try {
					prxCfg.setExpires(Integer.parseInt(tempExpire));
				} catch (NumberFormatException nfe) {
					throw new LinphoneCoreException(nfe);
				}
			}

			prxCfg.enableAvpf(tempAvpfEnabled);
			prxCfg.setAvpfRRInterval(tempAvpfRRInterval);
			prxCfg.enableQualityReporting(tempQualityReportingEnabled);
			prxCfg.setQualityReportingCollector(tempQualityReportingCollector);
			prxCfg.setQualityReportingInterval(tempQualityReportingInterval);

//			String regId = LinphonePreferences.instance().getPushNotificationRegistrationID();
//			String appId = LinphonePreferences.instance().getString(R.string.push_sender_id);
//			if (regId != null && LinphonePreferences.instance().isPushNotificationEnabled()) {
//				String contactInfos = "app-id=" + appId + ";pn-type=" + LinphonePreferences.instance().getString(R.string.push_type) + ";pn-tok=" + regId + ";pn-silent=1";
//				prxCfg.setContactUriParameters(contactInfos);
//			}

			if(tempPrefix != null){
				prxCfg.setDialPrefix(tempPrefix);
			}


			if(tempRealm != null)
				prxCfg.setRealm(tempRealm);

//			tempUserId +="@hu.ctcims.cn";
//			tempUsername+="@hu.ctcims.cn";

			LinphoneAuthInfo authInfo =lcFactory .createAuthInfo(tempUsername, tempUserId, tempPassword, tempHa1, tempRealm, tempDomain);
			authInfo.setUserId(tempUserId+ "@hu.ctcims.cn");
			lc.addProxyConfig(prxCfg);
			lc.addAuthInfo(authInfo);
			lc.setDefaultProxyConfig(prxCfg);
			if (!tempNoDefault)
				lc.setDefaultProxyConfig(prxCfg);
		}
	}