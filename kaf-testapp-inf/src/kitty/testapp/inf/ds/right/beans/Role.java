package kitty.testapp.inf.ds.right.beans;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.dao.table.TableDef;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.trade.pack.HttpRequest;
import kitty.kaf.helper.StringHelper;
import kitty.testapp.inf.ds.right.beans.Func;
import kitty.testapp.inf.ds.right.FuncHelper;

/**
 * 
 * 角色
 * 
 */
public class Role extends IdTableObject<Integer> {

    //[autogenerated:static(CACHE_KEY_PREFIX) statements=1]
    public static final String CACHE_KEY_PREFIX = "$cache.role";

    //[autogenerated:static(tableDef) statements=2]
    public static final TableDef tableDef = new TableDef("t_role");

    static {
        tableDef.getColumns().put("lastModifiedTime", new TableColumnDef(0, "最后修改时间", "last_modified_time", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("creationTime", new TableColumnDef(1, "创建时间", "creation_time", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("isDeleted", new TableColumnDef(2, "是否删除", "is_deleted", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("roleId", new TableColumnDef(3, "角色ID", "role_id", 0, 0, 0, false, "seq_role", false, true, false));
        tableDef.getColumns().put("roleDesp", new TableColumnDef(4, "角色描述", "role_desp", 0, 50, 0, false, null, false, true, true));
        tableDef.setPkColumns("roleId");
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
        if (columnName.equalsIgnoreCase("role_id")) return getId();
        else if (columnName.equalsIgnoreCase("role_desp")) return getRoleDesp();
        return super.getByColumn(columnName);
    }

    /**
     * 角色描述
     */
    private String roleDesp;

    /**
     * 获得角色ID
     */
    public Integer getRoleId() {
        //[autogenerated:return(getRoleId) statements=1]
        return getId();
    }

    /**
     * 获得角色描述
     */
    public String getRoleDesp() {
        //[autogenerated:return(getRoleDesp) statements=1]
        return roleDesp;
    }

    /**
     * 设置角色ID
     */
    public void setRoleId(Integer v) {
        //[autogenerated:body(setRoleId) statements=1]
        setId(v);
    }

    /**
     * 设置角色描述
     */
    public void setRoleDesp(String v) {
        //[autogenerated:body(setRoleDesp) statements=1]
        roleDesp = v;
    }

    @Override
    public String toString() {
        //[autogenerated:return(toString) statements=1]
        return roleDesp;
    }

    @Override
    public String getIdString() {
        //[autogenerated:return(getIdString) statements=1]
        return Integer.toString(getId());
    }

    @Override
    public void setIdString(String v) {
        //[autogenerated:body(setIdString) statements=1]
        setId(Integer.valueOf(v));
    }

    @Override
    public int compareId(Integer id1, Integer id2) {
        //[autogenerated:return(compareId) statements=1]
        return id1.compareTo(id2);
    }

    @Override
    public Role newInstance() {
        //[autogenerated:return(newInstance) statements=1]
        return new Role();
    }

    @Override
    public void setNull(boolean v) {
        //[autogenerated:return(setNull) statements=2]
        super.setNull(v);
        if (v) {
            setId(-1);
        }
    }

    @Override
    public void toJson(JSONObject json) throws JSONException {
        //[autogenerated:body(toJson) statements=2]
        super.toJson(json);
        json.put("role_desp", roleDesp);
    }

    @Override
    public void doReadFromStream(DataRead stream) throws IOException {
        //[autogenerated:body(doReadFromStream) statements=3]
        setId(stream.readInt());
        setRoleDesp(stream.readPacketByteLenString());
        setFuncIdList(stream.readLongList());
    }

    @Override
    public void doWriteToStream(DataWrite stream) throws IOException {
        //[autogenerated:body(doWriteToStream) statements=3]
        stream.writeInt(getId());
        stream.writePacketByteLenString(getRoleDesp());
        stream.writeLongList(getFuncIdList());
    }

    @Override
    public void readFromDb(DaoResultSet rset) throws SQLException {
        //[autogenerated:body(readFromDb) statements=3]
        super.readFromDb(rset);
        setId(rset.getInt("role_id"));
        setRoleDesp(rset.getString("role_desp"));
    }

    @Override
    public void readFromRequest(HttpRequest request, boolean isCreate) throws Exception {
        //[autogenerated:body(readFromRequest) statements=4]
        super.readFromRequest(request, isCreate);
        setId(request.getParameterIntDef("role_id", null));
        setRoleDesp(request.getParameter("role_desp"));
        setFuncIdList(StringHelper.splitToLongList(request.getParameter("func_id_list"), ","));
    }

    private List<Long> funcIdList;

    public List<Long> getFuncIdList() {
        //[autogenerated:return(getFuncIdList) statements=1]
        return funcIdList;
    }

    public void setFuncIdList(List<Long> v) {
        //[autogenerated:body(setFuncIdList) statements=1]
        funcIdList = v;
    }

    private List<Func> funcList;

    public List<Func> getFuncList() {
        //[autogenerated:return(getFuncList) statements=2]
        if (funcList == null && funcIdList != null) funcList = FuncHelper.localFuncMap.gets(funcIdList);
        return funcList;
    }
}
