/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.connector.%inject:project.connector%;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.ecore.EObject;

import fr.obeo.smartea.archimate.ArchimateElement;
import fr.obeo.smartea.core.basemm.BaseFactory;
import fr.obeo.smartea.core.basemm.Folder;
import fr.obeo.smartea.demo.connector.file.Folders;

/**
 * The FolderService class provides utility methods for working with Folders.
 * 
 * @author %inject:user.name%
 */
public class Folders {

	private static final BaseFactory BASE_FACTORY = BaseFactory.eINSTANCE;

	/**
     * Retrieves or creates nested folders based on the provided folder and ID.
     *
     * @param folder The root Folder from which to start the creation or retrieval of nested folders.
     * @param id     The ID representing the path of nested folders to retrieve or create.
     * 
     * @return The Folder at the specified path. If it doesn't exist, it creates the nested folders along the path.
     */
	public static Folder obtainFolders(Folder folder, String id) {
        return Arrays.stream(id.split("/"))
                .filter(it -> !it.isBlank())
                .reduce(folder, Folders::obtainFolder, (parent, current) -> current);
    }
	
	/**
    * Retrieves or creates a folder with the given ID inside the provided parent folder.
    *
    * @param folder The parent Folder in which to find or create the nested folder.
    * @param id     The ID of the folder to retrieve or create.
    * 
    * @return The Folder with the specified ID if it exists in the parent Folder, otherwise creates a new one.
    */
	public static Folder obtainFolder(Folder folder, String id) {
	    // suspicious: 
	    // folder.getId() + "/" + id
	    // or
	    // id
		return getFolderById(folder, folder.getId() + "/" + id)
		        .map(Folder.class::cast)
				.orElseGet(() -> createFolder(folder, id));
	}

	/**
     * Creates a new Folder inside the provided parent Folder with the given name.
     *
     * @param parentFolder The parent Folder in which to create the new Folder.
     * @param name         The name for the new Folder.
     * 
     * @return The newly created Folder.
     */
	public static Folder createFolder(Folder parentFolder, String name) {
		Folder result = BASE_FACTORY.createFolder();
		result.setName(name.toUpperCase());
		parentFolder.getFolders().add(result);
		return result;
	}
	

	/**
     * Retrieves a Folder from the given CDOResource based on its ID.
     *
     * @param semanticResource The CDOResource containing the Folder to be retrieved.
     * @param id               The ID of the Folder to retrieve.
     * 
     * @return The Folder with the specified ID if it exists in the CDOResource.
     * 
     * @throws IllegalArgumentException If the Folder with the given ID does not exist in the CDOResource.
     */
	public static Folder getFolderById(CDOResource semanticResource, String id) {
		EObject found = semanticResource.getEObject(id);
		if (!(found instanceof Folder)) {
			throw new IllegalArgumentException("Folder with ID " + id + " does not exists.");
		}
		return (Folder) found;
	}

	/**
     * Retrieves an ArchimateElement from the given Folder based on its ID.
     *
     * @param folder The Folder to search for the ArchimateElement.
     * @param id     The ID of the ArchimateElement to retrieve.
     * 
     * @return The ArchimateElement with the specified ID if it exists in the Folder.
     */
	public static Optional<ArchimateElement> getElementById(Folder folder, String id) {
		return folder.getElements().stream()
		        .filter(ArchimateElement.class::isInstance)
				.map(ArchimateElement.class::cast)
				.filter(element -> element.getId().equalsIgnoreCase(id))
				.findFirst();
	}

	/**
     * Retrieves a nested Folder from the given parent Folder based on its ID.
     *
     * @param folder The parent Folder to search for the nested Folder.
     * @param id     The ID of the nested Folder to retrieve.
     * 
     * @return The nested Folder with the specified ID if it exists in the parent Folder.
     */
	public static Optional<Folder> getFolderById(Folder folder, String id) {
		return folder.getFolders().stream()
		        .filter(element -> element.getId().equalsIgnoreCase(id))
		        .findFirst();
	}
}
