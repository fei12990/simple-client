package pers.luofei.http.client.codec;

/**
 * Created by luofei on 2017/9/8.
 */
public class CodecException extends Exception {

	private static final long serialVersionUID = -1143565158886477114L;

	public CodecException(String s, Exception e) {
        super(s, e);
    }

    public CodecException(Exception e) {

        super(e);
    }
}
