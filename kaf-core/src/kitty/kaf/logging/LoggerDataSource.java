package kitty.kaf.logging;

/**
 * 日志数据源，用于输出特殊格式或敏感日志
 * 
 * @author 赵明
 * 
 */
public interface LoggerDataSource {
	Object getLogData();
}
