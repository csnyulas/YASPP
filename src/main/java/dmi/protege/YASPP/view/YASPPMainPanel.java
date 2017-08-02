package dmi.protege.YASPP.view;


//import static com.hp.hpl.jena.assembler.JA.FileManager;
/*import static com.hp.hpl.jena.assembler.JA.FileManager;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter; */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.logging.Level;
import org.protege.editor.owl.rdf.SparqlInferenceFactory;
import org.protege.editor.owl.rdf.SparqlReasoner;
import org.protege.editor.owl.rdf.SparqlReasonerException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasoner;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;


public class YASPPMainPanel extends JPanel 
  {  
     static final Logger log = LoggerFactory.getLogger(YASPPMainPanel.class);
     SparqlReasoner reasoner;
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
     OWLEditorKit editorKit;
    public YASPPMainPanel(OWLEditorKit kit)
     {
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
   
   class ExecuteActionListener implements ActionListener
   {   
     
    @Override
    public void actionPerformed(ActionEvent event)
      {    
        try
          {
            SparqlResultSet set = reasoner.executeQuery(queryArea.getText());
            for(int i=0; i<set.getColumnCount(); i++)
                for(int j=0; j<set.getRowCount(); j++)
                    log.info(set.getResult(i, j).toString());
//        OWLReasonerManager reasonerManager =  editorKit.getOWLModelManager().getOWLReasonerManager();
//        ReasonerUtilities.warnUserIfReasonerIsNotConfigured(editorKit.getOWLWorkspace(), reasonerManager);
//        if (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED)
//         {        
//           try
//           {
//            OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
//            OWLOntology infOnt=manager.createOntology();
//            OWLDataFactory datafactory= manager.getOWLDataFactory();
//            List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
//            gens.add(new InferredSubClassAxiomGenerator());
//            gens.add(new InferredClassAssertionAxiomGenerator());
//            gens.add( new InferredDisjointClassesAxiomGenerator());
//            gens.add( new InferredEquivalentClassAxiomGenerator());
//            gens.add( new InferredEquivalentDataPropertiesAxiomGenerator());
//            gens.add( new InferredEquivalentObjectPropertyAxiomGenerator());
//            gens.add( new InferredInverseObjectPropertiesAxiomGenerator());
//            gens.add( new InferredObjectPropertyCharacteristicAxiomGenerator());
//            gens.add( new InferredPropertyAssertionGenerator());
//            gens.add( new InferredSubDataPropertyAxiomGenerator());
//            gens.add( new InferredSubObjectPropertyAxiomGenerator());
//            InferredOntologyGenerator iog = new InferredOntologyGenerator(editorKit.getModelManager().getReasoner(), gens);
//            iog.fillOntology(datafactory, infOnt);
//            for(OWLOntology o : editorKit.getOWLModelManager().getActiveOntologies())
//              {
//                for(OWLAnnotationAssertionAxiom ax : o.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
//                    manager.applyChange(new AddAxiom(infOnt, ax));
//                }
//              }
//            File output = File.createTempFile("temp", "owl");
//            IRI documentIRI2 = IRI.create(output);
//            manager.saveOntology(infOnt, documentIRI2);
//            FileReader reader= new FileReader(output);
//            Query q = QueryFactory.create(queryArea.getText());            
//            QueryExecution qe = QueryExecutionFactory.create( q, FileManager.getModel().read(reader, "defaultText"));
//            ResultSet rs = qe.execSelect(); 
//            ResultSetFormatter.out( rs );
//            log.info(rs.toString());
//          }
//        catch (OWLOntologyCreationException | IOException | OWLOntologyStorageException ex)   
//          {
//
//          }
            //  }
          } catch (SparqlReasonerException ex)
          {
            java.util.logging.Logger.getLogger(YASPPMainPanel.class.getName()).log(Level.SEVERE, null, ex);
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

 
