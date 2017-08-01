package dmi.protege.YASPP.tab;

import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YASPPTab extends OWLWorkspaceViewsTab {

	private static final Logger log = LoggerFactory.getLogger(YASPPTab.class);

	public YASPPTab() {
		setToolTipText("YASPP: Yet Another SPARQL Protege Plugin");
	}

    @Override
	public void initialise() {
		super.initialise();
		log.info("YASPP Tab initialized");
	}

	@Override
	public void dispose() {
		super.dispose();
		log.info("YASPP Tab disposed");
	}
}
