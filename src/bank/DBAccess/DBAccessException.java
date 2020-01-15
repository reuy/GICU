package bank.DBAccess;

public class DBAccessException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 int errorcode = 0;
	/*0 = No code, 
	 * 1 = User not found*/
	
    public DBAccessException(String errorMessage) {
        super(errorMessage);
    }
    public DBAccessException(int ErrCode, String errorMessage) {
        super(errorMessage);
        errorcode=ErrCode;
    }
    
    public int code() {
    	return errorcode;
    }
}

class NoPermissionException extends DBAccessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*0 = No code, 
	 * 1 = User not registered
	 * 2 = User not an employee */
	
    public NoPermissionException(String errorMessage) {
        super(errorMessage);
    }
    public NoPermissionException(int ErrCode, String errorMessage) {
        super(errorMessage);
        errorcode=ErrCode;
}
}
