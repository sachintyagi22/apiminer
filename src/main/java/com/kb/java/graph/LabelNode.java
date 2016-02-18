package com.kb.java.graph;

public class LabelNode implements Node {
    private int id;
    private String label;

    public LabelNode(int id, String label) {
        super();
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object pLabelNode) {
        if (pLabelNode != null &&
                pLabelNode instanceof LabelNode) {
            LabelNode labelNode = (LabelNode) pLabelNode;
            return this.getLabel().equals(labelNode.getLabel());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.label.hashCode();
    }
}
