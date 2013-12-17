<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<c:script scriptScope="varPrefixL1"></c:script>
<c:div styleClass="content" rendered="${${template.page.right}}">
	<c:div id="ctable">
		<c:div id="ctablelist" styleClass='list'>
		</c:div>
	</c:div>
	<c:div id="cpanel">
	</c:div>
	<c:script
		scriptText="var items=${mysession.user.orgTreeNode.jsonString3 }"></c:script>
	<script>
		var urlprefix = '/webtrade?executor=webtrade&group=right&cmd=';
		Object.merge(varPrefixL1, {
			setDisabled : function(b, d) {
				if (this[b])
					this[b].setDisabled(d);
			},
			init : function() {
				var self = this;
				self.tree = new UITreeControl({
					'parent' : 'ctablelist',
					'listPanelParams' : {
						'class' : 'k_treetable'
					},
					'listItemParams' : {
						'createParams' : {
							'class' : 'k_treetableitem'
						},
						'classPrefix' : 'k_treetableitem'
					},
					'requestParams' : {
						'textfield' : 'text',
						'url' : '/webtrade?executor=webtrade&group=right&cmd=getOrgChildren'
					},
					'items' : items,
					'input' : 'tree',
					'multiselect' : false,
					'events' : {
						'itemCreated' : function(item) {
							var p = item.captionLabel.getParent();
							var data = item.options['data'];
							item.addButton = new Element('a', {
								'text' : '添加',
								'href' : 'javascript:void(0);'
							}).inject(p).addEvent('click', function() {
								self.createOrEdit(data.id);
							});
							item.editButton = new Element('a', {
								'text' : '编辑',
								'href' : 'javascript:void(0);'
							}).inject(p).inject(p).addEvent('click', function() {
								self.createOrEdit(data.id, data.id);
							});
							self.createDelButton(item);
						}
					}
				});
				this.editPanel = new UIEditPanel({
					'jsCssFileMgr' : jsCssFileMgr,
					'panel' : 'cpanel',
					'buttons' : [ {
						'name' : 'save',
						'text' : '保存'
					}, {
						'name' : 'cancel',
						'text' : '取消',
						'init_show' : true
					} ],
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
							if (self.option == 'add') {
								var r = self.tree.add(o['data']['data']);
								if (r.parentNode && r.parentNode.delButton) {
									self.removeDelButton(r.parentNode);
								}
							} else {
								self.tree.update(o['data']);
							}
						}
					}
				});
				this.editPanel.dispose();
			},
			search : function() {
				$('search_text').blur();

			},
			createOrEdit : function(pid, id) {
				this.option = id != undefined ? 'edit' : 'add';
				this.editPanel.open({
					'title' : this.option == 'edit' ? '编辑机构部门' : '创建新机构部门',
					'cur_element' : 'ctable',
					'save_url' : urlprefix + (id != undefined ? 'editOrg' : 'insertOrg'),
					'url' : '/pages/right/org/create.go?parent_id=' + pid + (id != undefined ? '&id=' + id : '')
				});
			},
			createDelButton : function(item) {
				if (item.deleteEnabled()) {
					var p = item.captionLabel.getParent(), self = this;
					item.delButton = new Element('a', {
						'text' : '删除',
						'href' : 'javascript:void(0);'
					}).inject(p).inject(p).addEvent('click', function() {
						self.remove(item);
					});
					item.delPrompt = new Element('span').inject(p);
				}
			},
			removeDelButton : function(item) {
				if (item.delButton) {
					item.delButton.dispose();
					delete item.delButton;
				}
				if (item.delPrompt) {
					item.delPrompt.dispose();
					delete item.delPrompt;
				}
			},
			remove : function(item) {
				if (!window.confirm('真的要删除该机构吗？'))
					return;
				var self = this, data = item.options.data;
				new JsonRequest({
					'url' : '/webtrade?executor=webtrade&group=right&cmd=removeOrg',
					onError : function(c) {
						if (item.delPrompt)
							item.delPrompt.set('text', '删除失败:' + c.errMsg);
					},
					onSuccess : function(r) {
						var r = self.tree.remove(data);
						if (r.parentNode)
							self.createDelButton(r.parentNode);
					}
				}).send('id_list=' + data['id']);
				if (item.delPrompt)
					item.delPrompt.set('text', '请稍候...');
			}
		});
		varPrefixL1.init();
	</script>
</c:div>
<c:div styleClass="el_error" rendered="${!${template.page.right}}">对不起，您没有${template.func_desp}管理的权限</c:div>
