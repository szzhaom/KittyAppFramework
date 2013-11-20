<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<c:script scriptScope="varPrefixL1"></c:script>
<c:div styleClass="content" rendered="${mysession.user.right.basicManageEnabled}">
	<c:div id="ctable">
		<c:div id='cbpanel'>
			<i:input type='search' id='search_text'
				rendered="${mysession.user.right.basicManageEnabled}"
				buttonClick="varPrefixL1.search();"
				placeHolder="输入文件主机描述搜索"></i:input>
			<i:button id='create_button' value='创建新文件主机'
				rendered="${mysession.user.right.basicManageEnabled}"
				onClick="varPrefixL1.createOrEdit();"
				styleClass="inline_block k_button commbutton"></i:button>
			<i:button id='delete_button' value='删除文件主机'
				rendered="${mysession.user.right.basicManageEnabled}"
				onClick="varPrefixL1.deleteSelected();"
				styleClass="inline_block k_button commbutton"></i:button>
		</c:div>
		<c:div id="ctablelist" styleClass='list'>
		</c:div>
	</c:div>
	<c:div id="cpanel">
	</c:div>
	<script>
		var urlprefix = '/webtrade?executor=webtrade&group=file&cmd=';
		Object.merge(varPrefixL1, {
			setDisabled : function(b, d) {
				if (this[b])
					this[b].setDisabled(d);
			},
			init : function() {
				var _this = this;
				this.table = new UITableControl({
					'desp' : '文件主机',
					'panel' : 'ctablelist',
					'prefix' : 'filehost',
					'requestParams' : {
						'textfield' : 'company_category_desp',
						'url' : urlprefix + 'queryFileHost'
					},
					'delete_url' : urlprefix + 'removeFileHost',
					'pagerecords' : 12,
					'pageselector' : {},
					'multiselect' : true,
					'columns' : [{width:50,field:'id',row_class:'tcenter',head_class:'tcenter',checkbox:true},{width:80,field:'id',row_class:'tcenter',head_class:'tcenter',caption:'文件主机ID'},{width:'auto',field:'file_host_desp',row_class:'tcenter',head_class:'tcenter',caption:'文件主机描述'},{width:150,field:'ftp_host',row_class:'tleft',head_class:'tleft',caption:'FTP主机名'},{width:120,field:'ftp_user',row_class:'tleft',head_class:'tleft',caption:'FTP登录用户'},{width:50,field:'ftp_port',row_class:'tleft',head_class:'tleft',caption:'FTP端口'},{width:80,field:'options',row_class:'tcenter',head_class:'tcenter',caption:'操作'}],
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
											_this.createOrEdit(d['id']);
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
				this.table.ps['url'] = urlprefix + 'queryFileHost&keyword='+encodeURIComponent($('search_text').get('value'));
				this.table.load(false);
			},
			createOrEdit : function(id) {
				this.option = id!=undefined ? 'edit' : 'add';
				this.editPanel.open({
					'title' : this.option == 'edit' ? '编辑文件主机' : '创建新文件主机',
					'cur_element' : 'ctable',
					'save_url' : urlprefix + (id!=undefined?'editFileHost':'insertFileHost'),
					'url' : '/pages/basic/filehost/create.go' + (id!=undefined ? '?id=' + id : '')
				});
			},
			deleteSelected : function() {
				this.table.delSelected();
			}
		});
		varPrefixL1.init();
	</script>
</c:div>
<c:div styleClass="el_error" rendered="${!mysession.user.right.basicManageEnabled}">对不起，您没有文件主机管理的权限</c:div>
