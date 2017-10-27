/*******************************************************************************
 * Copyright (c) 2016 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.commons.libs.core.message;

import org.eclipse.rap.rwt.RWT;

/**
 * Commons message
 * 
 * @author hangum
 *
 */
public class CommonMessages {
	private static final String BUNDLE_NAME = "com.hangum.tadpole.commons.libs.core.message.messages"; //$NON-NLS-1$

	public String TimeLeft;
	public String DaysLeft;

	/** 저장 하시겠습니까? */
	public String doYouWantTosave;
	
	public String User;

	public String OK;
	public String Confirm;
	public String Cancel;
	
	public String Information;
	public String Warning;
	public String Error;
	public String Close;
	
	public String Yes;
	public String No;
	
	public String Start;
	public String Stop;
	
	public String Search;
	public String Filter;
	
	public String Add;
	public String Delete;
	public String Modify;
	public String ModifyMessage;
	public String Save;
	public String Run;
	public String General;
	
	public String Refresh;
	public String Clear;
	
	public String Others; // Others, 기타
	
	public String Title;
	public String Description;
	public String DescriptionATag;
	public String Email;
	public String Name;
	public String Date;
	public String CreateTime;
	
	// 공통 출력 메시지. 
	// 정의 명령이 완료 되었습니다.)
	public String CommandCoompleted;
	
		
	// 회사 정보
	public String CompanyInfo;
	
	/** 접근제어 시스템 연동 오류 */
	public String Check_DBAccessSystem;
	
	/** ?값은 ?보다 크고 ?보다 적어야 합니다.*/
	public String ValueIsLessThanOrOverThan;

	public String Text_ValueIsLessThanOrOverThan;
	
	/* ?보다 커야 합니다 */
	public String ValueIsOverThan;
	public String mustBeNumber;
	
	public String Please_InputText;

	public String ThisFunctionEnterprise;
	
	public String CantModifyPreferenc;
	
	public String Download;
	public String ResourceDownload;

	public String Role;

	public String DatabaseInformation;
	
	public String Database;
	
	public String Databases;
	
	public String DatabaseList;

	public String StartDate;

	public String EndDate;

	public String Authentication;
	
	public String TwoStepVerfication;

	public String ID;

	public String Test;

	public String IsIncorrect;

	public String ChangingErrorMsg;

	public String ChangingValue;

	public String DownloadIsComplete;

	public String UserRequestQuery;
	
	public String FileNotFound;
	
	public String TermExpired;
	
	public String TermExpiredMsg;

	public String UserRole;

	public String Prev;
	
	public String Next;

	public String ExportCSV;

	public String ExportExcel;
	
	public String OperationSuccessfullyCompleted;
	
	public String EnterYourEmailAddress;
	
	public String EnableGoogleAuthenticator;
	public String GoogleAuthenticator;
	public String GoogleAuthenticatorSettings;
	
	public String SecretKey; /* Google Authenticator */
	public String QRCode; /* Google Authenticator */
	public String OTP; /* Google Authenticator */
	
	public String Enable; /* Enable */
	
	public String Password; 
	public String ChangePassword;

	public String List;

	public String Type;

	public String Schema;

	public String TotalRowIs;
	
	public String LoadingData;
	public String PasswdOldNewIsSame;
	public String ThisIsReadOnlyDatabase;

	public String EnterNumbersOnly;
	public String EnterNumbersOnlyWithItem;
	
	public String Days;
	
	public String Version;
	
	/* [%s] must be greater than or equal to %d and less than or equal to %d. */
	public String InvalidRange_GEAndLEWithItem; 
	
	public String EnterItem;
	
	/* License */
	public String EnterpriseLicense;
	public String OpensourceLicense;
	public String LicenseType;
	public String ActivationDate;
	public String ExpirationDate;
	public String Remaining;
	
	/* Company */
	public String EmailCustomerSupport;
	public String TadpoleHubWebsite;
	public String ThankYouForUsingTadpoleDBHub;
	public String AboutTadpoleDBHub;
	public String Manual;
	
	public String TadpoleAdministrator;
	
	public String StartTime; 

	
	public static CommonMessages get() {
		return RWT.NLS.getISO8859_1Encoded(BUNDLE_NAME, CommonMessages.class);
	}

	private CommonMessages() {
	}
}
