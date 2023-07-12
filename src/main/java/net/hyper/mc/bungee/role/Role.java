package net.hyper.mc.bungee.role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role {
    private String name;
    private String tag;
    private int order;
    private int partysize;
    private int multiplier;
    private boolean multicolor;
    private boolean jumpqueue;
    private String permission;
}
