package %inject:project.fullname%.design.tool

import %inject:project.fullname%.design.Activator
import java.nio.file.Paths
import org.eclipse.core.runtime.Platform
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.XMIResource
import org.junit.Test
import org.mypsycho.modit.emf.sirius.tool.ODesignVerifications
import %inject:project.fullname%.design.description.%inject:project.prefix%Design

/**
 * Tool to generate Sirius design model in 'sirius' plugin.
 * <p>
 * Tool also reverse the created model to allows round-trip when design file is modified
 * directly.
 * </p>
 *
 * @author nperansin
 */
class DesignGeneration extends DesignToolBase {

	protected static val GENREVERSE_PATH = "target/gen"
	val resBundle = Platform.getResourceBundle(Activator.^default.bundle)
	
	protected static val TARGET_CLASS = %inject:project.prefix%Design
	
	@Test
    def void writeODesign() throws Exception {
    	System.setProperty("modit.registry.debug", "true")
    	
		println("\n### Generating ###\n")

        val res = new ResourceSetImpl()
        	.createResource(URI.createFileURI(ODESIGN_FILE.toString))
        
        val content = TARGET_CLASS.getDeclaredConstructor()
        	.newInstance()
        	.loadContent(res)
        	.head
                                
        res.save(#{ XMIResource.OPTION_ENCODING -> "UTF-8" })
        
        println(ODESIGN_FILE.fileName + " is updated.")
        println(">>" + ODESIGN_FILE)
        
        println("\n### Validating ###\n")
        ODesignVerifications.printValidation(TARGET_CLASS.simpleName, content)
        
		println("\n### I18N ###\n")
	 	ODesignVerifications.printI18nReport(content, resBundle)
        
        println()
        println()
        DesignReverse.createSiriusReverseIt(
			content, 
			Paths.get(GENREVERSE_PATH), 
			TARGET_CLASS.name
		).perform		
    }

}
