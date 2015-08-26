package org.swrlapi.test;

import checkers.nullness.quals.NonNull;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.exceptions.SWRLAPIException;
import org.swrlapi.factory.SWRLAPIFactory;

import java.io.File;
import java.util.Optional;

public class SWRLRuleEngineMinimalApp
{
  public static void main(@NonNull String[] args)
  {
    if (args.length > 1)
      Usage();

    Optional<File> owlFile = args.length == 0 ? Optional.empty() : Optional.of(new File(args[0]));

    try {
      OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
      OWLOntology ontology = owlFile.isPresent() ?
        ontologyManager.loadOntologyFromOntologyDocument(owlFile.get()) :
        ontologyManager.createOntology();
      OWLDocumentFormat format = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
      DefaultPrefixManager prefixManager = new DefaultPrefixManager();
      if (format.isPrefixOWLOntologyFormat())
        prefixManager.copyPrefixesFrom(format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());

      SWRLRuleEngine ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology, prefixManager);

      ruleEngine.infer();

    } catch (OWLOntologyCreationException e) {
      if (owlFile.isPresent())
        System.err
          .println("Error creating OWL ontology from file " + owlFile.get().getAbsolutePath() + ": " + e.getMessage());
      else
        System.err.println("Error creating OWL ontology: " + e.getMessage());
      System.exit(-1);
    } catch (SWRLAPIException e) {
      System.err.println("SWRLAPI error: " + e.getMessage());
      System.exit(-1);
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + SWRLRuleEngineMinimalApp.class.getName() + " [ <owlFileName> ]");
    System.exit(1);
  }
}