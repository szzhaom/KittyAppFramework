package kitty.testapp.inf.ds.file.beans;

import java.io.IOException;
import java.sql.SQLException;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.dao.table.TableDef;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.json.JSONException;
import kitty.kaf.trade.pack.HttpRequest;
import kitty.kaf.json.JSONObject;
import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.cache.LocalCachable;

/**
 * 
 * 文件分类
 * 
 */
public class FileCategory extends IdTableObject<Short> implements LocalCachable<Short> {

    //[autogenerated:static(CACHE_KEY_PREFIX) statements=1]
    public static final String CACHE_KEY_PREFIX = "$cache.filecategory";

    //[autogenerated:static(tableDef) statements=2]
    public static final TableDef tableDef = new TableDef("t_file_category", "文件分类");

    static {
        tableDef.getColumns().put("lastModifiedTime", new TableColumnDef(0, "最后修改时间", "last_modified_time", 7, 0, 0, false, null, false, 0, false, 1, null, null, null, null, false, false));
        tableDef.getColumns().put("creationTime", new TableColumnDef(1, "创建时间", "creation_time", 7, 0, 0, false, null, false, 0, false, 1, null, null, null, null, false, false));
        tableDef.getColumns().put("isDeleted", new TableColumnDef(2, "是否删除", "is_deleted", 0, 0, 0, false, null, false, 0, false, 1, null, null, null, null, false, false));
        tableDef.getColumns().put("fileCategoryId", new TableColumnDef(3, "文件分类ID", "file_category_id", 2, 5, 0, false, null, false, 0, false, 1, null, null, null, null, false, false));
        tableDef.getColumns().put("fileCategoryDesp", new TableColumnDef(4, "文件分类描述", "file_category_desp", 8, 50, 0, false, null, false, 0, true, 1, null, null, "由1~5050个字符组成,一个中文占2个字符", null, false, false));
        tableDef.getColumns().put("curFileHostId", new TableColumnDef(5, "当前的文件主机ID", "cur_file_host_id", 2, 5, 0, false, null, false, 0, false, 1, null, null, null, null, false, false));
        tableDef.setPkColumns("fileCategoryId");
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
        if (columnName.equalsIgnoreCase("file_category_id")) return getId();
        else if (columnName.equalsIgnoreCase("file_category_desp")) return getFileCategoryDesp();
        else if (columnName.equalsIgnoreCase("cur_file_host_id")) return getCurFileHostId();
        return super.getByColumn(columnName);
    }

    /**
     * 文件分类描述
     */
    private String fileCategoryDesp;

    /**
     * 当前的文件主机ID
     */
    private Short curFileHostId;

    /**
     * 获得文件分类ID
     */
    public Short getFileCategoryId() {
        //[autogenerated:return(getFileCategoryId) statements=1]
        return getId();
    }

    /**
     * 获得文件分类描述
     */
    public String getFileCategoryDesp() {
        //[autogenerated:return(getFileCategoryDesp) statements=1]
        return fileCategoryDesp;
    }

    /**
     * 获得当前的文件主机ID
     */
    public Short getCurFileHostId() {
        //[autogenerated:return(getCurFileHostId) statements=1]
        return curFileHostId;
    }

    /**
     * 设置文件分类ID
     */
    public void setFileCategoryId(Short v) {
        //[autogenerated:body(setFileCategoryId) statements=1]
        setId(v);
    }

    /**
     * 设置文件分类描述
     */
    public void setFileCategoryDesp(String v) {
        //[autogenerated:body(setFileCategoryDesp) statements=1]
        fileCategoryDesp = v;
    }

    /**
     * 设置当前的文件主机ID
     */
    public void setCurFileHostId(Short v) {
        //[autogenerated:body(setCurFileHostId) statements=1]
        curFileHostId = v;
    }

    @Override
    public String toString() {
        //[autogenerated:return(toString) statements=1]
        return fileCategoryDesp;
    }

    @Override
    public String getIdString() {
        //[autogenerated:return(getIdString) statements=1]
        return Short.toString(getId());
    }

    @Override
    public void setIdString(String v) {
        //[autogenerated:body(setIdString) statements=1]
        setId(Short.valueOf(v));
    }

    @Override
    public int compareId(Short id1, Short id2) {
        //[autogenerated:return(compareId) statements=1]
        return id1.compareTo(id2);
    }

    @Override
    public FileCategory newInstance() {
        //[autogenerated:return(newInstance) statements=1]
        return new FileCategory();
    }

    @Override
    public void setNull(boolean v) {
        //[autogenerated:return(setNull) statements=2]
        super.setNull(v);
        if (v) {
            setId((short)-1);
        }
    }

    @Override
    public void toJson(JSONObject json) throws JSONException {
        //[autogenerated:body(toJson) statements=4]
        super.toJson(json);
        json.put("file_category_desp", fileCategoryDesp);
        json.put("cur_file_host_id", curFileHostId);
        json.put("text", fileCategoryDesp);
    }

    @Override
    public void doReadFromStream(DataRead stream) throws IOException {
        //[autogenerated:body(doReadFromStream) statements=4]
        super.doReadFromStream(stream);
        setId(stream.readShort());
        setFileCategoryDesp(stream.readPacketByteLenString());
        setCurFileHostId(stream.readShort());
    }

    @Override
    public void doWriteToStream(DataWrite stream) throws IOException {
        //[autogenerated:body(doWriteToStream) statements=4]
        super.doWriteToStream(stream);
        stream.writeShort(getId());
        stream.writePacketByteLenString(getFileCategoryDesp());
        stream.writeShort(getCurFileHostId());
    }

    @Override
    public void readFromDb(DaoResultSet rset) throws SQLException {
        //[autogenerated:body(readFromDb) statements=4]
        super.readFromDb(rset);
        setId(rset.getShort("file_category_id"));
        setFileCategoryDesp(rset.getString("file_category_desp"));
        setCurFileHostId(rset.getShort("cur_file_host_id"));
    }

    @Override
    public void readFromRequest(HttpRequest request, boolean isCreate) throws Exception {
        //[autogenerated:body(readFromRequest) statements=4]
        super.readFromRequest(request, isCreate);
        setId(tableDef.test("fileCategoryId", request.getParameterShort("file_category_id"), isCreate));
        setFileCategoryDesp(tableDef.test("fileCategoryDesp", request.getParameter("file_category_desp"), isCreate));
        setCurFileHostId(tableDef.test("curFileHostId", request.getParameterShort("cur_file_host_id"), isCreate));
    }
}
