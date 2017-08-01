package dmi.protege.YASPP.view;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YASPPInputView extends AbstractOWLViewComponent {

    private static final Logger log = LoggerFactory.getLogger(YASPPInputView.class);

    private YASPPMainPanel yaspppanel;

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new GridLayout(1,1));        
        yaspppanel = new YASPPMainPanel(getOWLEditorKit());        
        add(yaspppanel);        
        log.info("Example View Component initialized");
        
    }

	@Override
	protected void disposeOWLView() {
		yaspppanel.dispose();
	}
}
