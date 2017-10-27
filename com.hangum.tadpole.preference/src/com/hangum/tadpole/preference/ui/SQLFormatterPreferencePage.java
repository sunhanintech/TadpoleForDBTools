/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.preference.ui;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.commons.libs.core.define.TadpoleProperties;
import com.hangum.tadpole.commons.libs.core.message.CommonMessages;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserInfoData;
import com.hangum.tadpole.preference.Messages;
import com.hangum.tadpole.preference.define.PreferenceDefine;
import com.hangum.tadpole.preference.get.GetPreferenceGeneral;
import com.hangum.tadpole.session.manager.SessionManager;

/**
 * SQL formatter preference page
 * 
 * @author hangum
 *
 */
public class SQLFormatterPreferencePage extends TadpoleDefaulPreferencePage implements IWorkbenchPreferencePage {
	private static final Logger logger = Logger.getLogger(SQLFormatterPreferencePage.class);
	
	private Combo comboTabsize;
	private Button btnNoInsertNewDecode;
	private Button btnNoInsertNewIn;
	private Button btnNewLineBeforeAndOr;
	private Button btnNewLineBeforeComma;
	private Button btnRemoveEmptyLine;
	private Button btnWordBreak;
	private Text textWidth;

	/**
	 * Create the preference page.
	 */
	public SQLFormatterPreferencePage() {
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		
		Label lblTabSize = new Label(container, SWT.NONE);
		lblTabSize.setText(Messages.get().TabWidth);
		
		comboTabsize = new Combo(container, SWT.READ_ONLY);
		comboTabsize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboTabsize.add("2"); //$NON-NLS-1$
		comboTabsize.add("4"); //$NON-NLS-1$
		comboTabsize.add("8"); //$NON-NLS-1$
		comboTabsize.select(0);
		
		btnNoInsertNewDecode = new Button(container, SWT.CHECK);
		btnNoInsertNewDecode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnNoInsertNewDecode.setText(Messages.get().SQLFormat_AddNewLineBeforeDECODE);
		btnNoInsertNewDecode.setToolTipText(Messages.get().SQLFormat_AddNewLineBeforeDECODE_ToolTip);
		
		btnNoInsertNewIn = new Button(container, SWT.CHECK);
		btnNoInsertNewIn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnNoInsertNewIn.setText(Messages.get().SQLFormat_AddNewLineBeforeIN);
		btnNoInsertNewIn.setToolTipText(Messages.get().SQLFormat_AddNewLineBeforeIN_ToolTip);
		
		btnNewLineBeforeAndOr = new Button(container, SWT.CHECK);
		btnNewLineBeforeAndOr.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnNewLineBeforeAndOr.setText(Messages.get().SQLFormat_AddNewLineBeforeANDOR);
		btnNewLineBeforeAndOr.setToolTipText(Messages.get().SQLFormat_AddNewLineBeforeANDOR_ToolTip);
		
		btnNewLineBeforeComma = new Button(container, SWT.CHECK);
		btnNewLineBeforeComma.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnNewLineBeforeComma.setText(Messages.get().SQLFormat_AddCommaBeforeNewLine);
		btnNewLineBeforeComma.setToolTipText(Messages.get().SQLFormat_AddCommaBeforeNewLine_ToolTip);
		
		btnRemoveEmptyLine = new Button(container, SWT.CHECK);
		btnRemoveEmptyLine.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRemoveEmptyLine.setText(Messages.get().SQLFormat_RemoveEmptyLines);
		
		btnWordBreak = new Button(container, SWT.CHECK);
		btnWordBreak.setText(Messages.get().MaximumNumberOfCharactersPerLine);
		
		textWidth = new Text(container, SWT.BORDER);
		textWidth.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				isValid();
			}
		});
		textWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		initDefaultValue();
		
		// google analytic
		AnalyticCaller.track(this.getClass().getName());

		return container;
	}
	
	@Override
	public boolean isValid() {
		String strTextWidth = textWidth.getText();
		
		if(!NumberUtils.isNumber(strTextWidth)) {
			textWidth.setFocus();
			
			setValid(false);
			setErrorMessage(CommonMessages.get().EnterNumbersOnly);
			return false;
		} else if(!((NumberUtils.toInt(strTextWidth) >= TadpoleProperties.NUMBER_OF_CHARACTERS_PER_LINE_MIN)
				 && (NumberUtils.toInt(strTextWidth) <= TadpoleProperties.NUMBER_OF_CHARACTERS_PER_LINE_MAX))) {
			textWidth.setFocus();

			setValid(false);
			setErrorMessage(String.format(CommonMessages.get().InvalidRange_GEAndLEWithItem, 
									 	 Messages.get().MaximumNumberOfCharactersPerLine, 
										 TadpoleProperties.NUMBER_OF_CHARACTERS_PER_LINE_MIN, 
										 TadpoleProperties.NUMBER_OF_CHARACTERS_PER_LINE_MAX));
			return false;
		}
		
		setErrorMessage(null);
		setValid(true);
		
		return true;
	}
	
	@Override
	public boolean performOk() {
		if(!isValid()) return false;
		
		String txtTabSize = comboTabsize.getText();
		String txtNoInsertDecode = ""+btnNoInsertNewDecode.getSelection(); //$NON-NLS-1$
		String txtNoInsertIn = ""+btnNoInsertNewIn.getSelection(); //$NON-NLS-1$
		
		String txtNewLineBefeoreAndOr = ""+btnNewLineBeforeAndOr.getSelection(); //$NON-NLS-1$
		String txtNewLineBefeoreComma = ""+btnNewLineBeforeComma.getSelection(); //$NON-NLS-1$
		String txtRemoveEmptyLine = ""+btnRemoveEmptyLine.getSelection(); //$NON-NLS-1$
		
		String txtWordbreak = ""+btnWordBreak.getSelection(); //$NON-NLS-1$
		String strTextWidth = textWidth.getText();
		
		// 테이블에 저장 
		try {
			TadpoleSystem_UserInfoData.updateSQLFormatterInfoData(
					txtTabSize, txtNoInsertDecode, txtNoInsertIn,
					txtNewLineBefeoreAndOr, txtNewLineBefeoreComma, txtRemoveEmptyLine,
					txtWordbreak, strTextWidth
					);
			
			// session 데이터를 수정한다.
			SessionManager.setUserInfo(PreferenceDefine.DEFAULT_TAB_SIZE_PREFERENCE, txtTabSize);
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_DECODE_PREFERENCE, txtNoInsertDecode);
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_IN_PREFERENCE, txtNoInsertIn);	
			
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_NEWLINE_BEFAORE_AND_OR_PREFERENCE, txtNewLineBefeoreAndOr);	
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_NEWLINE_BEFAORE_COMMA_PREFERENCE, txtNewLineBefeoreComma);	
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_REMOVE_EMPTY_LINE_PREFERENCE, txtRemoveEmptyLine);	
			
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_WORD_BREAK_PREFERENCE, txtWordbreak);	
			SessionManager.setUserInfo(PreferenceDefine.SQL_FORMATTER_WORD_WIDTH_PREFERENCE, strTextWidth);	
			
		} catch(Exception e) {
			logger.error("SQLFormatter preference saveing", e); //$NON-NLS-1$
			
			MessageDialog.openError(getShell(), CommonMessages.get().Confirm, Messages.get().RDBPreferencePage_5 + e.getMessage()); //$NON-NLS-1$
			return false;
		}
		
		return super.performOk();
	}

	/**
	 * initialize default value
	 */
	private void initDefaultValue() {
		comboTabsize.setText(GetPreferenceGeneral.getDefaultTabSize());
		
		btnNoInsertNewDecode.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatDecode()));
		btnNoInsertNewIn.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatIn()));
		
		btnNewLineBeforeAndOr.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatNewLineBeforeAndOr()));
		btnNewLineBeforeComma.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatNewLineBeforeComma()));
		btnRemoveEmptyLine.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatRemoveEmptyLine()));
		
		btnWordBreak.setSelection(Boolean.parseBoolean(GetPreferenceGeneral.getSQLFormatWordBreak()));
		textWidth.setText(GetPreferenceGeneral.getSQLFormatWordWidth());
		
	}

	@Override
	public boolean performCancel() {
		initDefaultValue();
		
		return super.performCancel();
	}
	
	@Override
	protected void performApply() {

		super.performApply();
	}
	
	@Override
	protected void performDefaults() {
		initDefaultValue();

		super.performDefaults();
	}

	
}
