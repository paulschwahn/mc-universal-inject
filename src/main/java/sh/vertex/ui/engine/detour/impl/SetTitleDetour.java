package sh.vertex.ui.engine.detour.impl;

import sh.vertex.ui.engine.detour.annotation.DetourFieldInjection;
import sh.vertex.ui.engine.detour.types.CancelableDetour;

public class SetTitleDetour extends CancelableDetour {

    private String title;

    @DetourFieldInjection(1)
    public String getTitle() {
        return title;
    }

    @DetourFieldInjection(1)
    public void setTitle(String title) {
        this.title = title;
    }
}
