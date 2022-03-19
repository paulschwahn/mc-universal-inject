package sh.vertex.ui.engine.mapping.discovery;

/**
 * "Clue" driven mapping finder
 *
 * for example: a class contains the string "Pre startup", with this clue
 * the MainWindow.class can be found.
 */
public interface ClueDiscoverer {

    Class<?> findUsingClues(MappingClue clues) throws Throwable;
}
