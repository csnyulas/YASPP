package dmi.protege.YASPP.view;
import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.hssf.usermodel.HSSFRow;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import org.protege.editor.owl.OWLEditorKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.protege.editor.owl.rdf.SparqlInferenceFactory;
import org.protege.editor.owl.rdf.SparqlReasoner;
import org.protege.editor.owl.rdf.SparqlReasonerException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;


public class YASPPMainPanel extends JPanel 
  {  
     static final Logger log = LoggerFactory.getLogger(YASPPMainPanel.class);
     SparqlReasoner reasoner;
     JTextPane queryArea;
     JTable outArea;
     YASPPTableModel model;
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
     OptionDialog optionD;
     String defaultText="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+"\n"+
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +"\n"+
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +"\n" +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +"\n" +
                        "SELECT ?subject ?object" +"\n" +
                        " WHERE { ?subject rdfs:subClassOf ?object }";
     OWLEditorKit editorKit;
     OptionConfig optionConfig;
    public YASPPMainPanel(OWLEditorKit kit)
     {
        optionConfig=new OptionConfig();
        editorKit=kit;        
        List<SparqlInferenceFactory> plugins = Collections.singletonList((SparqlInferenceFactory) new BasicSparqlReasonerFactory());
	reasoner = plugins.iterator().next().createReasoner(editorKit.getOWLModelManager().getOWLOntologyManager());
         try
           {
             reasoner.precalculate();
           } catch (SparqlReasonerException ex)
           {
             log.info(ex.toString());
           }
        setLayout(new GridLayout(1,1));
        queryArea=new JTextPane();
        queryArea.setText(defaultText);
        scrollNorthArea=new JScrollPane(queryArea);
        
        execute= new JButton("Execute");
        execute.addActionListener(new ExecuteActionListener());
        export= new JButton("Export");
        export.addActionListener(new ExportActionListener());
        exportOpz = new JButton("Option");
        exportOpz.addActionListener(new OptionActionListener());
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
        
        
             
        model = new YASPPTableModel(0, 2);
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
      
        optionD=new OptionDialog(null);
     } 
    
    
    public void dispose() {
       if (reasoner != null) {
			reasoner.dispose();
			reasoner = null;}
    }

    class ErrorQueryMessage extends JDialog
      {
          private JPanel envir;
          private JButton okbut;
          public ErrorQueryMessage(String message, String title)
            {
               setTitle(title);
               setVisible(false);         
               setModal(true);                
               setSize(400,200);
               setPreferredSize(new Dimension(400,200));
               setResizable(false);
               envir=new JPanel();
               envir.setLayout(new BorderLayout());
               JTextPane tex=new JTextPane();
               tex.setText(message);
               tex.setEditable(false);
                            
               addWindowListener(new WindowAdapter() {
                     @Override
                     public void windowClosing(WindowEvent e) {
                                    dispose(); //do something
                         }});
               setDefaultCloseOperation(DISPOSE_ON_CLOSE);
               
               okbut=new JButton("OK");
               okbut.addActionListener(new ActionListener(){
                                        @Override
                                        public void actionPerformed(ActionEvent e)
                                          {                                            
                                            dispose();
                                          }
                                   });
               envir.add(new JScrollPane(tex),BorderLayout.CENTER);
               JPanel buttonPanel=new JPanel(new FlowLayout());
               buttonPanel.add(okbut);
               envir.add(buttonPanel,BorderLayout.SOUTH);
               add(envir);
               setLocationRelativeTo(null);
            }          
      }
       
  class OptionActionListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
          { 
           optionD.setVisible(true);
           
          }
    
    }
    
    class ExportActionListener implements ActionListener
    {

        private void toJSON(YASPPTableModel model,  BufferedWriter bw) throws IOException
          {            
            StringBuilder result = new StringBuilder();
            result.append("{" + "\"head\": {");
            result.append("\"vars\": [");
            for(int i=0; i<model.getColumnCount(); i++)
              {
                result.append("\"").append(model.getColumnName(i)).append("\"");
                if(i!=model.getColumnCount()-1)
                   result.append(",");
              }
            result.append("]"); //close vars
            result.append("},"); //close head
            result.append("\"results\": { ");
            result.append(" \"bindings\": ["); //open bindings
            bw.write(result.toString());
            for(int i=0; i < model.getRowCount(); i++)
               {
                result=new StringBuilder();
                result.append("{");
                for(int j=0; j < model.getColumnCount(); j++)
                 {
                   result.append("\"").append(model.getColumnName(j)).append("\":");
                   result.append("{");
                   result.append("\"type\":");                   
                   if(!model.getValueAt(i, j).toString().startsWith("\""))
                      {
                        result.append("\"uri\",\"value\":\"");                          
                        result.append(model.getValueAt(i, j).toString()).append("\"");
                      }
                    else
                      {  
                        result.append("\"literal\",\"value\":"); 
                        String value= model.getValueAt(i, j).toString();                         
                        int dataMark= value.indexOf("^");
                        String type="";
                        int initMark=dataMark+3;
                        int finMark=1;
                        if(dataMark>=0)       
                          {
                            type="datatype";
                          }
                        else
                          {
                            dataMark=value.indexOf("@"); 
                            type="xml:lang";
                            initMark=dataMark+1;
                            finMark=0;
                          }
                           String tmp=value.substring(0, dataMark);                            
                           result.append(tmp);
                        
                           result.append(",\"").append(type).append("\":\"");
                           result.append(value.substring(initMark, value.length()-finMark));
                           result.append("\"");
                          
                       }
                     result.append("}");
                     if(j!=model.getColumnCount()-1)
                     result.append(",");
                  }
                 result.append("}");
                 if(i!=model.getRowCount()-1)
                   result.append(",");
                 bw.write(result.toString());
               }
                       
            bw.write("]}}");            
          }

        @Override
        public void actionPerformed(ActionEvent e)
          { 
            if(model.getDataVector().isEmpty())
                 {
                   JOptionPane.showMessageDialog(null, "No query executed. Execute a query.");
                   return;
                 }
            JFileChooser chooser = new JFileChooser();     
            int retrival = chooser.showSaveDialog(null);
            if (retrival == JFileChooser.APPROVE_OPTION)
             {               
              try 
               {
                  switch (optionConfig.format)
                     {
                          case 0:
                             {                   
                             // FileWriter fw = new FileWriter(chooser.getSelectedFile()+".xls");
                              HSSFWorkbook workbook = new HSSFWorkbook();
                              HSSFSheet sheet = workbook.createSheet(chooser.getSelectedFile().getName()+".xls");  
                              for(int i=0; i < model.getRowCount(); i++)
                                {
                                  HSSFRow rowhead = sheet.createRow((short)i);
                                  
                                  for(int j=0; j < model.getColumnCount(); j++)
                                    {
                                      rowhead.createCell(j).setCellValue(model.getValueAt(i,j).toString());                                       
                                    }
                                  
                                }
                               for(int i=0; i< model.getColumnCount(); i++)
                                   sheet.autoSizeColumn(i); 
                               FileOutputStream fileOut = new FileOutputStream(chooser.getSelectedFile()+".xls");
                               workbook.write(fileOut);
                               fileOut.close();
                               break;                       
                             }                     
                          case 1: 
                              {     
                                BufferedWriter bw = 
                                    new BufferedWriter(new FileWriter(chooser.getSelectedFile()+".srj", true));
                                toJSON(model, bw);                                 
                                bw.close();
                                break;  
                              } 
                          default: break;
                    }
                 }
                 catch (IOException ex)
                   {
                     log.info("Error on writing Query on file.");
                   }
              }           
          }    
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
             log.info("Error on reading Query file.");
           }
        }
      }   
     }
   
   class ExecuteActionListener implements ActionListener
   {   
        private String[][] createData(SparqlResultSet set)
          {
            String data[][]=new String[set.getRowCount()][set.getColumnCount()];
            for(int i=0; i<set.getColumnCount(); i++)
                for(int j=0; j<set.getRowCount(); j++)
                    data[j][i]=(set.getResult(j,i).toString());
            return data;
          }
     
    @Override
    public void actionPerformed(ActionEvent event)
      {    
        try
          {            
            SparqlResultSet set = reasoner.executeQuery(queryArea.getText());                      
            String data[][];                  
            
            String names[]=new String[set.getColumnCount()];        
            for(int i=0; i<set.getColumnCount();i++)
                names[i]=set.getColumnName(i);
            
            data=createData(set);
            model.setDataVector(data, names);
            model.fireTableDataChanged();
          } 
        catch (SparqlReasonerException ex)
          {            
           ErrorQueryMessage messaged=new ErrorQueryMessage(ex.toString(), "Error on Query");
           messaged.setVisible(true);
           
          }
    }
  
  }

   
public class OptionDialog extends JDialog
  {
    JPanel topPanel;
    JPanel bottomPanel;
    JComboBox formatBox;
    JButton okbutton;
    
    public OptionDialog (JFrame parent)
     {
         super(parent, "Options");
         setSize(300,150);
         setPreferredSize(new Dimension(300,150));
         setVisible(false);         
         setModal(true);        
         setLayout(new BorderLayout());
         addWindowListener(new WindowAdapter() {
                     @Override
                     public void windowClosing(WindowEvent e) {
                                    closeDiscard(); //do something
                         }});
         
         
         formatBox=new JComboBox(new String[]{"Microsoft Excel", "SPARQL JSON"});
         formatBox.setPreferredSize(new Dimension(200, formatBox.getPreferredSize().height));
         
         optionConfig=new OptionConfig(formatBox.getSelectedIndex());
         
         okbutton=new JButton("OK");
         okbutton.addActionListener(new ActionListener(){
                                        @Override
                                        public void actionPerformed(ActionEvent e)
                                          {
                                            optionConfig.save(formatBox.getSelectedIndex());
                                            dispose();
                                          }
                                   });
                 
         
         topPanel=new JPanel();
         topPanel.setLayout(new FlowLayout());
         topPanel.add(new JLabel("Export Format"));
         topPanel.add(formatBox);
         
         bottomPanel=new JPanel();
         bottomPanel.setLayout(new FlowLayout());
         bottomPanel.add(okbutton);
         
         add(topPanel, BorderLayout.NORTH);
         add(bottomPanel, BorderLayout.SOUTH);
         setLocationRelativeTo(null);
     }  
    public void closeDiscard()
      {
          formatBox.setSelectedIndex(optionConfig.format);
          dispose();
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

class OptionConfig
  {
     int format;
     public OptionConfig(){ format=-1;}
     public OptionConfig(int value){format=value;}
     public void save(int _format){format=_format;}
  }

 
