package uni.fmi.tourguide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class PlovdivOntology {
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private SimpleShortFormProvider shortFormProvider;

    public PlovdivOntology(String path) {
        try {
            manager = OWLManager.createOWLOntologyManager();
            dataFactory = manager.getOWLDataFactory();
            File file = new File(path);
            ontology = manager.loadOntologyFromOntologyDocument(file);
            shortFormProvider = new SimpleShortFormProvider();
            System.out.println("Ontology loaded: " + ontology.getOntologyID());
        } catch (Exception e) {
            System.out.println("Failed to load ontology");
            e.printStackTrace();
        }
    }

    public List<String> getTourLocations(String interestFilter, int maxDuration, String mobilityOption) {
        List<String> results = new ArrayList<>();
        String baseIRI = "http://www.semanticweb.org/user/ontologies/2025/5/plovdiv#";

        OWLClass tourLocationClass = dataFactory.getOWLClass(IRI.create(baseIRI + "TourLocation"));
        OWLNamedIndividual interestInd = dataFactory.getOWLNamedIndividual(IRI.create(baseIRI + interestFilter));
        OWLNamedIndividual mobilityInd = dataFactory.getOWLNamedIndividual(IRI.create(baseIRI + mobilityOption));

        OWLObjectProperty hasInterest = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "hasInterest"));
        OWLObjectProperty suitableForMobility = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + "suitableForMobility"));
        OWLDataProperty hasVisitDuration = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "hasVisitDuration"));
        OWLDataProperty hasName = dataFactory.getOWLDataProperty(IRI.create(baseIRI + "hasName"));

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();

        for (OWLNamedIndividual individual : individuals) {
            boolean isTourLocation = false;
            for (OWLClassAssertionAxiom ax : ontology.getClassAssertionAxioms(individual)) {
                if (ax.getClassesInSignature().contains(tourLocationClass)) {
                    isTourLocation = true;
                    break;
                }
            }
            if (!isTourLocation) continue;

            boolean matchesInterest = false;
            boolean matchesMobility = false;
            int visitDuration = Integer.MAX_VALUE;
            String locationName = individual.getIRI().getFragment();

            for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(individual)) {
                if (ax.getProperty().equals(hasInterest) && ax.getObject().equals(interestInd)) {
                    matchesInterest = true;
                }
                if (ax.getProperty().equals(suitableForMobility) && ax.getObject().equals(mobilityInd)) {
                    matchesMobility = true;
                }
            }

            for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(individual)) {
                if (ax.getProperty().equals(hasVisitDuration)) {
                    try {
                        visitDuration = Integer.parseInt(ax.getObject().getLiteral());
                    } catch (NumberFormatException e) {
                        visitDuration = Integer.MAX_VALUE;
                    }
                }
                if (ax.getProperty().equals(hasName)) {
                    locationName = ax.getObject().getLiteral();
                }
            }

            if (matchesInterest && matchesMobility && visitDuration <= maxDuration) {
                results.add(locationName + " (" + visitDuration + " min)");
            }
        }

        return results;
    }
}
