package io.dispatchframework.javabaseimage;

public class Response {
    private Context context;
    private Object payload;

    public Response(Context context, Object payload) {
        this.context = context;
        this.payload = payload;
    }

    public Context getContext() {
        return context;
    }

    public Object getPayload() {
        return payload;
    }
}
