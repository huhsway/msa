package com.my.bookcatalog;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.my.bookcatalog");

        noClasses()
            .that()
                .resideInAnyPackage("com.my.bookcatalog.service..")
            .or()
                .resideInAnyPackage("com.my.bookcatalog.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.my.bookcatalog.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
