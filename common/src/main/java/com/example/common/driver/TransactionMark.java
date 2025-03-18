package com.example.common.driver;

public class TransactionMark {

    private String tid;  // transaction ID
    private TransactionType type;  // transaction type
    private int actorId;  // actor ID
    private MarkStatus status;  // transaction status
    private String source;  // transaction source

    // construct function without parameters
    public TransactionMark() {}

    // construct function with parameters
    public TransactionMark(String tid, TransactionType type, int actorId, MarkStatus status, String source) {
        this.tid = tid;
        this.type = type;
        this.actorId = actorId;
        this.status = status;
        this.source = source;
    }

    // Getters and Setters
    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public MarkStatus getStatus() {
        return status;
    }

    public void setStatus(MarkStatus status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "TransactionMark{" +
                "tid='" + tid + '\'' +
                ", type=" + type +
                ", actorId=" + actorId +
                ", status=" + status +
                ", source='" + source + '\'' +
                '}';
    }
}
