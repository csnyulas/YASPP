package dmi.protege.YASPP.view;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class YASPPMainPanel extends JPanel 
  {  
     static final Logger log = LoggerFactory.getLogger(YASPPMainPanel.class);
     JTextPane queryArea;
     JTable outArea;
     JPanel buttonArea;
     JPanel southPanel;
     JScrollPane scrollNorthArea;
     JScrollPane scrollSouthArea;     
     JSplitPane splitter;
     JButton execute;
     JButton export;
     JButton exportOpz;
     JButton importQ;
     JButton saveQ;
     String defaultText="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+"\n"+
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n"+
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +"\n" +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +"\n" +
                        "SELECT ?subject ?object" +"\n" +
                        " WHERE { ?subject rdfs:subClassOf ?object }";
     
    public YASPPMainPanel()
     {
        setLayout(new GridLayout(1,1));
        queryArea=new JTextPane();
        queryArea.setText(defaultText);
        scrollNorthArea=new JScrollPane(queryArea);
        
        execute= new JButton("Execute");
        export= new JButton("Export");
        exportOpz = new JButton("Option");
        importQ = new JButton("Import");
        importQ.addActionListener(new ImportActionListener());
        saveQ=new JButton("Save");
        saveQ.addActionListener(new SaveActionListener());
        buttonArea = new JPanel(new FlowLayout());
        buttonArea.add(execute);
        buttonArea.add(saveQ);        
        buttonArea.add(importQ);
        buttonArea.add(export);
        buttonArea.add(exportOpz);
        
        
             
        YASPPTableModel model = new YASPPTableModel(1, 2);
        model.setColumnIdentifiers(new String[]{"?subject","?object"});
        outArea=new JTable(model);
        
        scrollSouthArea=new JScrollPane(outArea);
        outArea.setFillsViewportHeight(true);
        
        southPanel=new JPanel(new BorderLayout());        
        southPanel.add(buttonArea, BorderLayout.NORTH);
        southPanel.add(scrollSouthArea, BorderLayout.CENTER);
        
        splitter=new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollNorthArea, southPanel); 
        splitter.setResizeWeight(0.7);
        add(splitter);
      
     } 
    
    
    public void dispose() {
       // modelManager.removeListener(modelListener);
       // refreshButton.removeActionListener(refreshAction);
    }
    
  class SaveActionListener implements ActionListener
  {    
    @Override
    public void actionPerformed(ActionEvent event)
      {
       JFileChooser chooser = new JFileChooser();       
      int retrival = chooser.showSaveDialog(null);
      if (retrival == JFileChooser.APPROVE_OPTION)
        {
         try 
          {
            FileWriter fw = new FileWriter(chooser.getSelectedFile()+".SPARQL");
            fw.write(queryArea.getText());
            fw.close();
          } 
         catch (IOException ex)
           {
             log.info("Error on writing Query on file.");
           }
        }
      }
    }
  
   class ImportActionListener implements ActionListener
   {    
    @Override
    public void actionPerformed(ActionEvent event)
      {
       JFileChooser chooser = new JFileChooser();       
      int retrival = chooser.showOpenDialog(null);
      if (retrival == JFileChooser.APPROVE_OPTION)
        {
         try 
          {
            FileReader fr = new FileReader(chooser.getSelectedFile());
	    BufferedReader br = new BufferedReader(fr);
   	    String sCurrentLine=null;
            String fstring="";
	    while ((sCurrentLine = br.readLine()) != null)             
	       fstring+=sCurrentLine+"\n";
	    queryArea.setText(fstring);

          } 
         catch (IOException ex)
           {
             log.info("Error on readin Query file.");
           }
        }
      }   
     }
  
  }


 class YASPPTableModel extends DefaultTableModel 
   {

    public YASPPTableModel(int x, int y)
      {
         super(x, y);
      }
           
    public boolean isCellEditable() {
          return false;
            }
     }

 
