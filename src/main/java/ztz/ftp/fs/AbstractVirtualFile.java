/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.fs;


public class AbstractVirtualFile {
	
	private final String name;
	private final Permission userPermission;
	private final Permission groupPermission;
	private final Permission otherPermission;
	
	public AbstractVirtualFile(String name, Permission userPermission, 
			 Permission groupPermission,
			 Permission otherPermission) {
		this.name = name;
		this.userPermission=userPermission;
		this.groupPermission=groupPermission;
		this.otherPermission=otherPermission;
	}
	
	public String getListString() {
		return String.format("%s%s%s%s 1 %d %d %10d %s %s\r\n",
				isDirectory()?"d":"-",userPermission.perm(),groupPermission.perm(),otherPermission.perm(),
						getUID(),getGID(),getSize(),getDate(),getName());
		
	}
	
	public int getUID() {
		return 1;
	}
	public int getGID() {
		return 1;
	}
	
	public boolean isDirectory() { return false; }
	public String getDate() { return "Jan 01 1970"; };
	
	public String getName() { return this.name; };
	public long getSize() { return 512; };

}
