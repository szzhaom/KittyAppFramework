package kitty.testapp.inf.ds.right.beans;

import java.io.IOException;
import java.sql.SQLException;
import kitty.kaf.cache.LocalCachable;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.dao.table.TableDef;
import kitty.kaf.io.Checkable;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.TreeNode;
import kitty.kaf.io.TreeNodeData;
import kitty.kaf.io.UnuqieKeyCachable;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.trade.pack.HttpRequest;

/**
 * 
 * 功能
 * 
 */
public class Func extends IdTableObject<Long> implements UnuqieKeyCachable<Long>, LocalCachable<Long>, TreeNodeData<Long>, Checkable {

    //[autogenerated:static(CACHE_KEY_PREFIX) statements=1]
    public static final String CACHE_KEY_PREFIX = "$cache.func";

    //[autogenerated:static(tableDef) statements=2]
    public static final TableDef tableDef = new TableDef("t_func");

    static {
        tableDef.getColumns().put("lastModifiedTime", new TableColumnDef(0, "最后修改时间", "last_modified_time", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("creationTime", new TableColumnDef(1, "创建时间", "creation_time", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("isDeleted", new TableColumnDef(2, "是否删除", "is_deleted", 0, 0, 0, false, null, false, false, false));
        tableDef.getColumns().put("funcId", new TableColumnDef(3, "功能ID", "func_id", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("funcCode", new TableColumnDef(4, "功能编码", "func_code", 0, 30, 0, true, null, false, true, false));
        tableDef.getColumns().put("funcDesp", new TableColumnDef(5, "功能描述", "func_desp", 0, 50, 0, false, null, false, true, true));
        tableDef.getColumns().put("funcDepth", new TableColumnDef(6, "功能树度", "func_depth", 0, 0, 0, false, null, false, true, false));
        tableDef.getColumns().put("allocEnabled", new TableColumnDef(7, "是否允许分配", "alloc_enabled", 0, 0, 0, false, null, false, true, false));
        tableDef.setPkColumns("funcId");
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
        if (columnName.equalsIgnoreCase("func_id")) return getId();
        else if (columnName.equalsIgnoreCase("func_code")) return getFuncCode();
        else if (columnName.equalsIgnoreCase("func_desp")) return getFuncDesp();
        else if (columnName.equalsIgnoreCase("func_depth")) return getFuncDepth();
        else if (columnName.equalsIgnoreCase("alloc_enabled")) return isAllocEnabled();
        return super.getByColumn(columnName);
    }

    /**
     * 功能编码
     */
    private String funcCode;

    /**
     * 功能描述
     */
    private String funcDesp;

    /**
     * 功能树度
     */
    private Integer funcDepth;

    /**
     * 是否允许分配
     */
    private Boolean allocEnabled;

    /**
     * 获得功能ID
     */
    public Long getFuncId() {
        //[autogenerated:return(getFuncId) statements=1]
        return getId();
    }

    /**
     * 获得功能编码
     */
    public String getFuncCode() {
        //[autogenerated:return(getFuncCode) statements=1]
        return funcCode;
    }

    /**
     * 获得功能描述
     */
    public String getFuncDesp() {
        //[autogenerated:return(getFuncDesp) statements=1]
        return funcDesp;
    }

    /**
     * 获得功能树度
     */
    public Integer getFuncDepth() {
        //[autogenerated:return(getFuncDepth) statements=1]
        return funcDepth;
    }

    /**
     * 获得是否允许分配
     */
    public Boolean isAllocEnabled() {
        //[autogenerated:return(isAllocEnabled) statements=1]
        return allocEnabled;
    }

    /**
     * 设置功能ID
     */
    public void setFuncId(Long v) {
        //[autogenerated:body(setFuncId) statements=1]
        setId(v);
    }

    /**
     * 设置功能编码
     */
    public void setFuncCode(String v) {
        //[autogenerated:body(setFuncCode) statements=1]
        funcCode = v;
    }

    /**
     * 设置功能描述
     */
    public void setFuncDesp(String v) {
        //[autogenerated:body(setFuncDesp) statements=1]
        funcDesp = v;
    }

    /**
     * 设置功能树度
     */
    public void setFuncDepth(Integer v) {
        //[autogenerated:body(setFuncDepth) statements=1]
        funcDepth = v;
    }

    /**
     * 设置是否允许分配
     */
    public void setAllocEnabled(Boolean v) {
        //[autogenerated:body(setAllocEnabled) statements=1]
        allocEnabled = v;
    }

    @Override
    public String toString() {
        //[autogenerated:return(toString) statements=1]
        return funcDesp;
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
    public Func newInstance() {
        //[autogenerated:return(newInstance) statements=1]
        return new Func();
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
        //[autogenerated:body(toJson) statements=5]
        super.toJson(json);
        json.put("func_code", funcCode);
        json.put("func_desp", funcDesp);
        json.put("func_depth", funcDepth);
        json.put("alloc_enabled", allocEnabled);
    }

    @Override
    public void doReadFromStream(DataRead stream) throws IOException {
        //[autogenerated:body(doReadFromStream) statements=5]
        setId(stream.readLong());
        setFuncCode(stream.readPacketByteLenString());
        setFuncDesp(stream.readPacketByteLenString());
        setFuncDepth(stream.readInt());
        setAllocEnabled(stream.readBoolean());
    }

    @Override
    public void doWriteToStream(DataWrite stream) throws IOException {
        //[autogenerated:body(doWriteToStream) statements=5]
        stream.writeLong(getId());
        stream.writePacketByteLenString(getFuncCode());
        stream.writePacketByteLenString(getFuncDesp());
        stream.writeInt(getFuncDepth());
        stream.writeBoolean(isAllocEnabled());
    }

    @Override
    public void readFromDb(DaoResultSet rset) throws SQLException {
        //[autogenerated:body(readFromDb) statements=6]
        super.readFromDb(rset);
        setId(rset.getLong("func_id"));
        setFuncCode(rset.getString("func_code"));
        setFuncDesp(rset.getString("func_desp"));
        setFuncDepth(rset.getInt("func_depth"));
        setAllocEnabled(rset.getBoolean("alloc_enabled"));
    }

    @Override
    public void readFromRequest(HttpRequest request, boolean isCreate) throws Exception {
        //[autogenerated:body(readFromRequest) statements=6]
        super.readFromRequest(request, isCreate);
        setId(request.getParameterLong("func_id"));
        setFuncCode(request.getParameter("func_code"));
        setFuncDesp(request.getParameter("func_desp"));
        setFuncDepth(request.getParameterInt("func_depth"));
        setAllocEnabled(request.getParameterBoolean("alloc_enabled"));
    }

    @Override
    public String getUniqueKey() {
        //[autogenerated:return(getUniqueKey) statements=1]
        return getFuncCode();
    }

    @Override
    public void setUniqueKey(String v) {
        //[autogenerated:body(setUniqueKey) statements=1]
        setFuncCode(v);
    }

    boolean checked;

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean newValue) {
        checked = newValue;
    }

    @Override
    public Long getParentId() {
        return null;
    }

    @Override
    public boolean isNullId(Long id) {
        return false;
    }

    @Override
    public Number getDepth() {
        return funcDepth;
    }

    @Override
    public void copyDataToTreeNode(TreeNode<?, ?> node) {
    }

    @Override
    public void copyDataFromTreeNode(TreeNode<?, ?> node) {
    }
}
