package kitty.testapp.inf.ds.right.beans;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.dao.table.TableDef;
import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.UnuqieKeyCachable;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.trade.pack.HttpRequest;
import kitty.testapp.inf.ds.right.FuncHelper;
import kitty.testapp.inf.ds.right.RoleHelper;
import kitty.testapp.inf.enumdef.Gender;
import kitty.testapp.inf.ds.right.beans.Role;

/**
 * 
 * 用户
 * 
 */
public class User extends IdTableObject<Long> implements UnuqieKeyCachable<Long>, kitty.kaf.session.SessionUser {

    //[autogenerated:static(CACHE_KEY_PREFIX) statements=1]
    public static final String CACHE_KEY_PREFIX = "$cache.user";

    //[autogenerated:static(tableDef) statements=2]
    public static final TableDef tableDef = new TableDef("t_user");

    static {
        tableDef.getColumns().put("lastModifiedTime", new TableColumnDef(0, "最后修改时间", "last_modified_time", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("creationTime", new TableColumnDef(1, "创建时间", "creation_time", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("isDeleted", new TableColumnDef(2, "是否删除", "is_deleted", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("userId", new TableColumnDef(3, "用户ID", "user_id", 0, 0, 0, false, "seq_user", false, true, false));
        tableDef.getColumns().put("userCode", new TableColumnDef(4, "用户编码", "user_code", 0, 30, 0, true, null, false, true, false));
        tableDef.getColumns().put("userName", new TableColumnDef(5, "用户名", "user_name", 0, 255, 0, false, null, false, true, true));
        tableDef.getColumns().put("userPwd", new TableColumnDef(6, "密码", "user_pwd", 0, 32, 0, false, null, true, true, false));
        tableDef.getColumns().put("birthday", new TableColumnDef(7, "生日", "birthday", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("gender", new TableColumnDef(8, "性别", "gender", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("loginCount", new TableColumnDef(9, "登录次数", "login_count", 0, 0, 0, false, null, false, false, false));
        tableDef.setPkColumns("userId");
    }

    private static final long serialVersionUID = 1L;

    @Override
    public TableDef getTableDef() {
        //[autogenerated:return(getTableDef) statements=1]
        return tableDef;
    }

    @Override
    public Object getByColumn(String columnName) {
        //[autogenerated:return(getByColumn) statements=2]
        if (columnName.equalsIgnoreCase("user_id")) return getId();
        else if (columnName.equalsIgnoreCase("user_code")) return getUserCode();
        else if (columnName.equalsIgnoreCase("user_name")) return getUserName();
        else if (columnName.equalsIgnoreCase("user_pwd")) return getUserPwd();
        else if (columnName.equalsIgnoreCase("birthday")) return getBirthday();
        else if (columnName.equalsIgnoreCase("gender")) return getGender();
        else if (columnName.equalsIgnoreCase("login_count")) return getLoginCount();
        return super.getByColumn(columnName);
    }

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String userPwd;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 获得用户ID
     */
    public Long getUserId() {
        //[autogenerated:return(getUserId) statements=1]
        return getId();
    }

    /**
     * 获得用户编码
     */
    public String getUserCode() {
        //[autogenerated:return(getUserCode) statements=1]
        return userCode;
    }

    /**
     * 获得用户名
     */
    public String getUserName() {
        //[autogenerated:return(getUserName) statements=1]
        return userName;
    }

    /**
     * 获得密码
     */
    public String getUserPwd() {
        //[autogenerated:return(getUserPwd) statements=1]
        return userPwd;
    }

    /**
     * 获得生日
     */
    public Date getBirthday() {
        //[autogenerated:return(getBirthday) statements=1]
        return birthday;
    }

    /**
     * 获得性别
     */
    public Gender getGender() {
        //[autogenerated:return(getGender) statements=1]
        return gender;
    }

    /**
     * 获得登录次数
     */
    public Long getLoginCount() {
        //[autogenerated:return(getLoginCount) statements=1]
        return loginCount;
    }

    /**
     * 设置用户ID
     */
    public void setUserId(Long v) {
        //[autogenerated:body(setUserId) statements=1]
        setId(v);
    }

    /**
     * 设置用户编码
     */
    public void setUserCode(String v) {
        //[autogenerated:body(setUserCode) statements=1]
        userCode = v;
    }

    /**
     * 设置用户名
     */
    public void setUserName(String v) {
        //[autogenerated:body(setUserName) statements=1]
        userName = v;
    }

    /**
     * 设置密码
     */
    public void setUserPwd(String v) {
        //[autogenerated:body(setUserPwd) statements=1]
        if (v != null && v.length() > 0) userPwd = v;
    }

    /**
     * 设置生日
     */
    public void setBirthday(Date v) {
        //[autogenerated:body(setBirthday) statements=1]
        birthday = v;
    }

    /**
     * 设置性别
     */
    public void setGender(Gender v) {
        //[autogenerated:body(setGender) statements=1]
        gender = v;
    }

    /**
     * 设置登录次数
     */
    public void setLoginCount(Long v) {
        //[autogenerated:body(setLoginCount) statements=1]
        loginCount = v;
    }

    @Override
    public String toString() {
        //[autogenerated:return(toString) statements=1]
        return userName;
    }

    private List<Integer> roleIdList;

    public List<Integer> getRoleIdList() {
        //[autogenerated:return(getRoleIdList) statements=1]
        return roleIdList;
    }

    public void setRoleIdList(List<Integer> v) {
        //[autogenerated:body(setRoleIdList) statements=1]
        roleIdList = v;
    }

    private List<Role> roleList;

    public List<Role> getRoleList() {
        //[autogenerated:return(getRoleList) statements=2]
        if (roleList == null && roleIdList != null) roleList = RoleHelper.roleMap.gets(roleIdList);
        return roleList;
    }

    private List<Integer> ownerRoleIdList;

    public List<Integer> getOwnerRoleIdList() {
        //[autogenerated:return(getOwnerRoleIdList) statements=1]
        return ownerRoleIdList;
    }

    public void setOwnerRoleIdList(List<Integer> v) {
        //[autogenerated:body(setOwnerRoleIdList) statements=1]
        ownerRoleIdList = v;
    }

    private List<Role> ownerRoleList;

    public List<Role> getOwnerRoleList() {
        //[autogenerated:return(getOwnerRoleList) statements=2]
        if (ownerRoleList == null && ownerRoleIdList != null) ownerRoleList = RoleHelper.roleMap.gets(ownerRoleIdList);
        return ownerRoleList;
    }

    @Override
    public String getIdString() {
        //[autogenerated:return(getIdString) statements=1]
        return Long.toString(getId());
    }

    @Override
    public void setIdString(String v) {
        //[autogenerated:body(setIdString) statements=1]
        setId(Long.valueOf(v));
    }

    @Override
    public int compareId(Long id1, Long id2) {
        //[autogenerated:return(compareId) statements=1]
        return id1.compareTo(id2);
    }

    @Override
    public User newInstance() {
        //[autogenerated:return(newInstance) statements=1]
        return new User();
    }

    @Override
    public void setNull(boolean v) {
        //[autogenerated:return(setNull) statements=2]
        super.setNull(v);
        if (v) {
            setId(-1L);
        }
    }

    @Override
    public void toJson(JSONObject json) throws JSONException {
        //[autogenerated:body(toJson) statements=6]
        super.toJson(json);
        json.put("user_code", userCode);
        json.put("user_name", userName);
        json.put("birthday", birthday);
        json.put("gender", gender.getValue());
        json.put("login_count", loginCount);
    }

    @Override
    public void doReadFromStream(DataRead stream) throws IOException {
        //[autogenerated:body(doReadFromStream) statements=9]
        setId(stream.readLong());
        setUserCode(stream.readPacketByteLenString());
        setUserName(stream.readPacketByteLenString());
        setUserPwd(stream.readPacketByteLenString());
        setBirthday(stream.readDate());
        setGender(Gender.readFromStream(stream));
        setLoginCount(stream.readLong());
        setRoleIdList(stream.readIntList());
        setOwnerRoleIdList(stream.readIntList());
    }

    @Override
    public void doWriteToStream(DataWrite stream) throws IOException {
        //[autogenerated:body(doWriteToStream) statements=9]
        stream.writeLong(getId());
        stream.writePacketByteLenString(getUserCode());
        stream.writePacketByteLenString(getUserName());
        stream.writePacketByteLenString(getUserPwd());
        stream.writeDate(getBirthday());
        Gender.writeToStream(getGender(), stream);
        stream.writeLong(getLoginCount());
        stream.writeIntList(getRoleIdList());
        stream.writeIntList(getOwnerRoleIdList());
    }

    @Override
    public void readFromDb(DaoResultSet rset) throws SQLException {
        //[autogenerated:body(readFromDb) statements=8]
        super.readFromDb(rset);
        setId(rset.getLong("user_id"));
        setUserCode(rset.getString("user_code"));
        setUserName(rset.getString("user_name"));
        setUserPwd(rset.getString("user_pwd"));
        setBirthday(rset.getDate("birthday"));
        setGender(Gender.valueOf(rset.getInt("gender")));
        setLoginCount(rset.getLong("login_count"));
    }

    @Override
    public void readFromRequest(HttpRequest request, boolean isCreate) throws Exception {
        //[autogenerated:body(readFromRequest) statements=9]
        super.readFromRequest(request, isCreate);
        setId(request.getParameterLongDef("user_id", null));
        setUserCode(request.getParameter("user_code"));
        setUserName(request.getParameter("user_name"));
        setUserPwd(SecurityHelper.md5(request.getParameter("user_pwd")));
        setBirthday(request.getParameterDate("birthday"));
        setGender(Gender.valueOf(request.getParameterInt("gender")));
        setRoleIdList(StringHelper.splitToIntList(request.getParameter("role_id_list"), ","));
        setOwnerRoleIdList(StringHelper.splitToIntList(request.getParameter("owner_role_id_list"), ","));
    }

    @Override
    public String getUniqueKey() {
        //[autogenerated:return(getUniqueKey) statements=1]
        return getUserCode();
    }

    @Override
    public void setUniqueKey(String v) {
        //[autogenerated:body(setUniqueKey) statements=1]
        setUserCode(v);
    }

    @Override
    public String getLoginName() {
        return userCode;
    }

    @Override
    public boolean hasRight(long right) {
        if (userCode.equals("admin")) return true;
        return funcIdList != null && funcIdList.contains(right);
    }

    private List<Long> funcIdList;

    public List<Long> getFuncIdList() {
        return funcIdList;
    }

    public void setFuncIdList(List<Long> funcIdList) {
        this.funcIdList = funcIdList;
    }

    UserAvailableRight right = new UserAvailableRight(this);

    public UserAvailableRight getRight() {
        return right;
    }

    FuncTreeNode userFuncTreeNode;

    public FuncTreeNode getUserFuncTreeNode() {
        if (userFuncTreeNode == null) {
            if (this.getRight().isAdmin()) userFuncTreeNode = FuncHelper.getFuncTreeNode();
            else {
                FuncTreeNode node = new FuncTreeNode();
                node.add(FuncHelper.localFuncMap.getItems(), true);
                node.enableList(getFuncIdList());
                userFuncTreeNode = node;
            }
        }
        return userFuncTreeNode;
    }

    /**
     * 登录次数
     */
    private Long loginCount = 0L;
}
