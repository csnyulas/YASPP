package dmi.protege.YASPP.view;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class YASPPMainPanel extends JPanel 
  {  
     JTextPane queryArea;
     JScrollPane scrollArea;
     JSplitPane splitter;
     
    public YASPPMainPanel()
     {
         setLayout(new GridLayout(1,1));
      queryArea=new JTextPane();
      scrollArea=new JScrollPane(queryArea);
      splitter=new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollArea,new JPanel());      
      add(splitter);
      
     } 
    
    
    public void dispose() {
       // modelManager.removeListener(modelListener);
       // refreshButton.removeActionListener(refreshAction);
    }
    
  
  }
