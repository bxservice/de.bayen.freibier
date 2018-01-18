package de.bayen.freibier.ui;

import org.adempiere.webui.AdempiereIdGenerator;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;

public class BAYMessageBox extends Window implements EventListener<Event>
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8928526331932742124L;

	private static final String MESSAGE_PANEL_STYLE = "text-align:left; word-break: break-all; overflow: auto; max-height: 350pt; min-width: 230pt; max-width: 450pt;";	
	private static final String SMALLER_MESSAGE_PANEL_STYLE = "text-align:left; word-break: break-all; overflow: auto; max-height: 350pt; min-width: 180pt; ";
	private String imgSrc = new String("");

	private Vlayout lblDiv = new Vlayout();

	private Button btnOk;
	private Image img = new Image();
	
	private int returnValue = 0;
	private ADWindowContent parentWindow;

	/** A OK button. */
	public static final int OK = 0x0001;

	/** A symbol consisting of a question mark in a circle. */
	public static final String QUESTION = "~./zul/img/msgbox/question-btn.png";

	/** A symbol consisting of an exclamation point in a triangle with a yellow background. */
	public static final String EXCLAMATION  = "~./zul/img/msgbox/warning-btn.png";

	/** A symbol of a lowercase letter i in a circle. */
	public static final String INFORMATION = "~./zul/img/msgbox/info-btn.png";

	/** A symbol consisting of a white X in a circle with a red background. */
	public static final String ERROR = "~./zul/img/msgbox/stop-btn.png";

	/** Contains no symbols. */
	public static final String NONE = null;

	public BAYMessageBox() {
		super();
		init();
	}

	private void init() {
		setSclass("popup-dialog");
		
		// Invert - Unify  OK/Cancel IDEMPIERE-77
		btnOk = ButtonFactory.createNamedButton(ConfirmPanel.A_OK);
		btnOk.setId("btnOk");
		btnOk.addEventListener(Events.ON_CLICK, this);

		Panel pnlMessage = new Panel();
		if (ClientInfo.maxWidth(399)) {
			pnlMessage.setStyle(SMALLER_MESSAGE_PANEL_STYLE);
			this.setWidth("100%");
		} else
			pnlMessage.setStyle(MESSAGE_PANEL_STYLE);

		pnlMessage.appendChild(lblDiv);
		ZKUpdateUtil.setHflex(pnlMessage, "min");
		
		Vbox pnlText = new Vbox();
		pnlText.appendChild(pnlMessage);

		Hbox pnlImage = new Hbox();

		ZKUpdateUtil.setWidth(pnlImage, "72px");
		pnlImage.setAlign("center");
		pnlImage.setPack("center");
		pnlImage.appendChild(img);
				
		Hbox north = new Hbox();
		north.setAlign("center");
		this.appendChild(north);		
		north.appendChild(pnlImage);
		north.appendChild(pnlText);
		north.setSclass("dialog-content");
		north.setWidth("100%");;

		Hbox pnlButtons = new Hbox();
		pnlButtons.setAlign("center");
		pnlButtons.setPack("end");
		pnlButtons.appendChild(btnOk);

		ZKUpdateUtil.setWidth(pnlButtons, "100%");
		this.appendChild(pnlButtons);
		pnlButtons.setSclass("dialog-footer");
		
		this.setBorder("normal");
		this.setContentStyle("background-color:#ffffff;");
		this.setPosition("left, top");
		
		String id = "MessageBox_"+AdempiereIdGenerator.escapeId("");
		this.setId(id);
	}

	public int show(String title, int buttons, String icon) {
		this.imgSrc = icon;
		img.setSrc(imgSrc);
		
		btnOk.setVisible(false);

		if ((buttons & OK) != 0)
			btnOk.setVisible(true);

		this.setTitle(title);
		this.setPosition("center");
		this.setClosable(true);
		this.setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
		this.setSizable(true);

		this.setVisible(true);
		if (parentWindow != null) {
			parentWindow.getComponent().appendChild(this);
			parentWindow.showBusyMask(this);
			LayoutUtils.openOverlappedWindow(this.getParent(), this, "middle_center");
		    this.focus();
		}
		else
			AEnv.showCenterScreen(this);

		return returnValue;
	}
	
	public void addLabelComponent(Component comp) {
		lblDiv.appendChild(comp);
	}
	
	public void addLabelComponent(String label) {
		lblDiv.appendChild(new Text(label));
	}
	
	public void setParentWindow(ADWindow window) {
		parentWindow = window.getADWindowContent();
	}

	public void onEvent(Event event) throws Exception
	{
		if (event == null)
			return;

		if (event.getTarget() == btnOk) {
			returnValue = OK;
		}

		try {
			this.detach();
		} catch (NullPointerException npe) {
			if (! (SessionManager.getSessionApplication() == null)) // IDEMPIERE-1937 - ignore when session was closed
				throw npe;
		}
	}
	
	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (parentWindow != null)
			parentWindow.hideBusyMask();
	}
}
