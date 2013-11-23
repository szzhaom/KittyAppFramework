<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<c:script scriptScope="varPrefixL1"></c:script>
<c:div styleClass="content" rendered="${${template.page.right}}">
	<c:div id="ctable">
		<c:div id='cbpanel'>
			<i:input type='search' id='search_text'
				rendered="${template.query_right}"
				buttonClick="varPrefixL1.search();"
				placeHolder="${template.place_holder}"></i:input>
			<i:button id='create_button' value='创建新${template.func_desp}'
				rendered="${template.create_right}"
				onClick="varPrefixL1.createOrEdit();"
				styleClass="inline_block k_button commbutton"></i:button>
			<i:button id='delete_button' value='删除${template.func_desp}'
				rendered="${template.delete_right}"
				onClick="varPrefixL1.deleteSelected();"
				styleClass="inline_block k_button commbutton"></i:button>
		</c:div>
		<c:div id="ctablelist" styleClass='list'>
		</c:div>
	</c:div>
	<c:div id="cpanel">
	</c:div>
	<script>
		var urlprefix = '/webtrade?executor=${template.trade.executor}&group=${template.trade.group}&cmd=';
		Object.merge(varPrefixL1, {
			setDisabled : function(b, d) {
				if (this[b])
					this[b].setDisabled(d);
			},
			init : function() {
				var _this = this;
				this.table = new UITableControl({
					'desp' : '${template.func_desp}',
					'panel' : 'ctablelist',
					'prefix' : 'filehost',
					'requestParams' : {
						'textfield' : 'company_category_desp',
						'url' : urlprefix + '${template.trade.queryCmd}'
					},
					'delete_url' : urlprefix + '${template.trade.deleteCmd}',
					'pagerecords' : 12,
					'pageselector' : {},
					'multiselect' : true,
					'columns' : [${template.talble.columns}],
					'events' : {
						'createField' : function(o) {
							var c = o['column'], d = o['item']['options']['data'],div;
							if (d==undefined)
								return;
							switch (c['field']) {
							case 'options': {
								o['element'] = new Element('div', {
									'class' : c['row_class']
								});
								div=new Element('div',{'class':'tfield'}).inject(o['element']);
								new Element('a', {
									'text' : '编辑',
									'href' : 'javascript:void(0);',
									'events' : {
										'mousedown' : function(e) {
											e.stop();
										},
										'mouseup' : function(e) {
											e.stop();
										},
										'click' : function(e) {
											e.stop();
											_this.createOrEdit(d['${template.pk}']);
										}
									}
								}).inject(div);
							}
								break;
							}
						},
						checkChanged : function() {
						}
					}
				});
				this.editPanel = new UIEditPanel({
					'jsCssFileMgr' : jsCssFileMgr,
					'panel' : 'cpanel',
					'buttons' : [{
						'name' : 'save',
						'text' : '保存'
					} , {
						'name' : 'cancel',
						'text' : '取消',
						'init_show' : true
					}],
					'events' : {
						'buttonClick' : function(o) {
							switch (o['name']) {
							case 'save':
								this.save();
								break;
							case 'cancel':
								this.cancel();
								break;
							}
						},
						'saveComplete' : function(o) {
							if (_this.option == 'add')
								_this.table.add(o['data']);
							else {
								_this.table.update(o['data']);
							}
						}
					}
				});
				this.editPanel.dispose();
			},
			search : function() {
				$('search_text').blur();
				this.table.load({'keyword':$('search_text').get('value')});
			},
			createOrEdit : function(id) {
				this.option = id!=undefined ? 'edit' : 'add';
				this.editPanel.open({
					'title' : this.option == 'edit' ? '编辑${template.func_desp}' : '创建新${template.func_desp}',
					'cur_element' : 'ctable',
					'save_url' : urlprefix + (id!=undefined?'${template.trade.editCmd}':'${template.trade.insertCmd}'),
					'url' : '${template.page.create}' + (id!=undefined ? '?id=' + id : '')
				});
			},
			deleteSelected : function() {
				this.table.delSelected();
			}
		});
		varPrefixL1.init();
	</script>
</c:div>
<c:div styleClass="el_error" rendered="${!${template.page.right}}">对不起，您没有${template.func_desp}管理的权限</c:div>
