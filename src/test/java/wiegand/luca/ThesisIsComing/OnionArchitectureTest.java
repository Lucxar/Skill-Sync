package wiegand.luca.ThesisIsComing;


import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class OnionArchitectureTest {

    private static final String BASE = "com.example";
    private static final JavaClasses CLASSES = new ClassFileImporter().importPackages(BASE);

    private static final String DOMAIN = "..domain..";
    private static final String APPLICATION = "..application..";
    private static final String INFRASTRUCTURE = "..infrastructure..";
    private static final String PRESENTATION = "..presentation..";

    @ArchTest
    static final ArchRule domain_must_not_depend_on_outer_layers =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(APPLICATION, INFRASTRUCTURE, PRESENTATION);

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure_or_presentation =
            noClasses().that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(INFRASTRUCTURE, PRESENTATION);

    @ArchTest
    static final ArchRule domain_and_application_must_not_depend_on_infrastructure =
            noClasses().that().resideInAnyPackage(DOMAIN, APPLICATION)
                    .should().dependOnClassesThat()
                    .resideInAPackage(INFRASTRUCTURE);

    @ArchTest
    static final ArchRule inner_layers_must_not_depend_on_presentation =
            noClasses().that().resideInAnyPackage(DOMAIN, APPLICATION, INFRASTRUCTURE)
                    .should().dependOnClassesThat()
                    .resideInAPackage(PRESENTATION);
}
