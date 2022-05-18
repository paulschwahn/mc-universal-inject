package sh.vertex.ui.engine.mapping.discovery;

import sh.vertex.ui.engine.structure.Proxy;

/**
 * "Clue" driven mapping finder
 *
 * for example: a class contains the string "Pre startup", with this clue
 * the MainWindow.class can be found.
 */
public abstract class ClassDiscoverer {

    public abstract Class<?> findUsingClues(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue clues) throws Throwable;

}
