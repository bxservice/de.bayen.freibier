package de.bayen.freibier.model;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.compiere.model.MRole;
import org.compiere.util.Env;
import org.osgi.service.event.Event;

public class FreiBierLoginEventHandler extends AbstractEventHandler {

	@Override
	protected void doHandleEvent(Event event) {
		if (event.getTopic().equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData eventData = getEventData(event);
			if (eventData.getAD_Client_ID() == 1000000 && eventData.getAD_Org_ID() == 0) {
				MRole role = MRole.get(Env.getCtx(), eventData.getAD_Role_ID());
				if (!role.getName().toLowerCase().contains("admin") || role.isManual()) {
					System.out.println("Must log in with an organization: " + role.getName());
					addErrorMessage(event, "Keine Organisation ausgewählt");
				}
			}
		}
	}

	@Override
	protected void initialize() {
		registerEvent(IEventTopics.AFTER_LOGIN);
	}
	
}
