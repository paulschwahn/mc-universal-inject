package sh.vertex.ui.engine.detour.impl;

import sh.vertex.ui.engine.detour.annotation.DetourFieldInjection;
import sh.vertex.ui.engine.detour.types.CancelableDetour;

public class ChatMessageDetour extends CancelableDetour {

    private String message;

    @DetourFieldInjection(1)
    public String getMessage() {
        return this.message;
    }

    @DetourFieldInjection(1)
    public void setMessage(String message) {
        this.message = message;
    }
}
