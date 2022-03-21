package sh.vertex.ui.engine.detour.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import sh.vertex.ui.engine.detour.Detour;

@Data
public class CancelableDetour implements Detour {

    private boolean canceled;
}
