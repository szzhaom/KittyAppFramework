/**
 * 验证器
 */
var ValidatorHelper = {
	validate : function(el) {
		el = $(el);
		var ls1 = el.getElements("input"), ls = [];
		for (var i = 0; i < ls1.length; i++) {
			if (ls1[i].get('is_validate_input') == 'true')
				ls.push(ls1[i]);
		}
		ls.combine(el.getElements('select'));
		ls.combine(el.getElements('textarea'));
		var ret = true;
		for (var i = 0; i < ls.length; i++) {
			var o = ls[i], r = o.get('regexp'), v = o.get('value'), b = true, max = o.get('maxlength'), min = o
					.get('minlength'), p = $('prompt_' + o.get('id')), maxv = o.get('maxvalue'), minv = o
					.get('minvalue'), ep = o.get('error_prompt'), type = o.get('ktype');
			if (o.get('validate_input')) {
				v = $(o.get('validate_input')).get('value');
				p = $('prompt_' + o.get('validate_input'));
			}
			var bl = v.getBytesLength();
			if (r != undefined) {
				b = new RegExp(r).test(v);
			}
			if (b) {
				if (max != undefined && max > 0) {
					b = bl <= max;
				}
			}
			if (b) {
				if (min != undefined) {
					b = bl >= min;
					if (!b && bl == 0)
						ep = '必须输入';
				}
			}
			if (!(type == 'datetime' || type == 'date')) {
				if (b) {
					if (Number.isNumber(maxv)) {
						b = Number.isNumber(v) && Number(v) <= Number(maxv);
					}
				}
				if (b) {
					if (Number.isNumber(minv)) {
						b = Number.isNumber(v) && Number(v) >= Number(minv);
					}
				}
			}
			p.addRemoveClass(b ? 'ok' : [ 'error', 'normal' ], b ? 'error' : [ 'ok', 'normal' ]);
			p.set('html', b ? '&nbsp;' : ep);
			if (!b) {
				if (ret)
					o.focus();
				ret = b;
			}

		}
		return ret;
	}
};
var UIEditPanel = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'k_editpanel'
		},
		'titleParams' : {
			'class' : 'title'
		},
		'buttonPanelParams' : {
			'class' : 'title_button'
		},
		'promptParams' : {
			'class' : 'prompt',
			'error_class' : 'prompt_error'
		},
		'buttons' : []
	},
	doCreate : function(options) {
		this.parent(options);
		this.$parent = this.panel.getParent();
		var self = this;
		this.panel.empty();
		this.titlePanel = new Element('div', options['titleParams']).inject(this.panel);
		this.title = new Element('span', {
			'html' : '&nbsp;'
		}).inject(this.titlePanel);
		this.buttonPanel = new Element('div', options['buttonPanelParams']).inject(this.titlePanel);
		this.buttons = {};
		for (var i = 0; i < options['buttons'].length; i++) {
			var s = options['buttons'][i];
			var a = new Element('a', {
				'href' : 'javascript:void(0)',
				'class' : s['class'],
				'text' : s['text'],
				'events' : {
					'click' : function(e) {
						self.fireEvent('buttonClick', this.context);
					}
				}
			});
			if (!s['hide'] && (s['init_show'] == undefined || s['init_show']))
				a.inject(this.buttonPanel);
			if (s['html'] != undefined)
				a.set('html', s['html']);
			a.context = s;
			this.buttons[s['name']] = a;
		}
		this.prompt = new Element('div', {
			'class' : this.options['promptParams']['prompt']
		}).inject(this.buttonPanel, 'top');
		this.content = new Element('div').inject(this.panel);
	},
	save : function(url) {
		if (this.$loading) {
			alert('正在装入，请稍候...');
			return;
		}
		if (this.saving)
			return;
		this.saving = true;
		var self = this, ps = this.options['promptParams'];
		var p = this.prompt;
		if (typeOf(this.validate) == 'function')
			if (!this.validate()) {
				this.saving = false;
				return;
			}
		var form = this.content.getElements('input')[0].form;
		var doError = function(c) {
			if (p) {
				p.addRemoveClass(ps['error_class'], ps['class']);
				p.set('text', '保存失败:' + c.errMsg);
			}
			self.saving = false;
		};
		var doSuccess = function(r) {
			if (p)
				p.set('text', '');
			if (self.fireEvent('saveComplete', r))
				self.show(false);
		};
		if (form.enctype != 'multipart/form-data' || window.FormData) {
			var data, m = 'post';
			if (form.enctype != 'multipart/form-data') {
				data = form.toQueryString();
				m = 'get';
			} else {
				data = new FormData(form);
			}
			new JsonRequest({
				urlEncoded : false,
				url : url ? url : this.openps['save_url'],
				method : m,
				onError : function(c) {
					doError(c);
				},
				onSuccess : function(r) {
					doSuccess(r);
				},
				'onUploadProgress' : function(e) {
					console.log(e);
				}
			}).send({
				data : data
			});
		} else {
			form.action = contextPath + (url ? url : this.openps['save_url']);
			form.method = 'post';
			var id = String.uniqueID();
			form.target = id;
			var iframe = new Element('iframe', {
				'id' : id
			}).inject(document.body).setStyle('display', 'none').addEvent('load', function() {
				var result = JSON.decode(this.contentDocument.body.innerText);
				iframe.dispose();
				if (!result.result.success) {
					doError({
						errMsg : result.result.message
					});
				} else {
					doSuccess({
						'data' : result
					});
				}
			});
			form.submit();
		}
		if (p) {
			p.addRemoveClass(ps['class'], ps['error_class']);
			p.set('text', '正在保存...');
		}
	},
	cancel : function() {
		this.show(false);
	},
	showButton : function(n, v) {
		var bs = this.options['buttons'], p, i;
		for (i = 0; i < bs.length; i++) {
			if (bs[i]['name'] == n) {
				p = bs[i];
				break;
			}
		}
		if (p && !v)
			p['hide'] = true;
		if (this.buttons != undefined) {
			var o = this.buttons[n];
			if (o) {
				if (v) {
					for (var j = i - 1; j >= 0; j--) {
						var b = this.buttons[bs[j]['name']];
						if (b && b.getParent())
							o.inject(b, 'after');
					}
					for (var j = i + 1; j < bs.length; j++) {
						var b = this.buttons[bs[j]['name']];
						if (b && b.getParent())
							o.inject(b, 'before');
					}
				} else if (o.getParent() != null) {
					o.dispose();
				}
			}
		}
	},
	show : function(b) {
		var ps = this.options;
		if (!this.$created)
			return;
		var p = this.curElement;
		this.showing = b;
		if (b) {
			p.dispose();
			this.panel.inject(this.$parent);
			var bs = ps['buttons'];
			for (var i = bs.length - 1; i >= 0; i--) {
				var ss = bs[i], b = this.buttons[ss['name']];
				if (b.getParent() == null && !ss['hide']
						&& (this.$loadstatus == 1 || (ss['init_show'] == undefined || ss['init_show']))) {
					var f = i == bs.length - 1 ? this.prompt : this.buttons[bs[i + 1]['name']];
					b.inject(f, 'before');
				}
			}
		} else {
			this.fireEvent('beforeClose');
			p.inject(this.$parent);
			this.content.empty();
			this.panel.dispose();
			this.fireEvent('closed');
		}
	},
	setPrompt : function(t) {
		if (this.prompt)
			this.prompt.set('text', t);
	},
	open : function(ps) {
		if (ps['buttons'])
			this.options['buttons'] = ps['buttons'];
		// this.create();
		var timer, p = this.panel, n = this.content, m = $(ps['cur_element']);
		this.curElement = m;
		this.setPrompt('');
		this.saving = false;
		if (this.title)
			this.title.set('text', ps['title']);
		this.openps = ps;
		this.$loadstatus = 0;
		var self = this;
		p.dispose();
		n.dynamicLoad({
			events : {
				'prepareUpdate' : function() {
					self.show(true);
				},
				'load' : function() {
					self.$loadstatus = 1;
					clearTimeout(timer);
				},
				'error' : function() {
					self.$loadstatus = 2;
					self.show(true);
					clearTimeout(timer);
					n.setLoadErrorPrompt();
				},
				'loading' : function() {
					self.$loadstatus = 0;
					timer = (function() {
						self.show(true);
						n.setLoadingPrompt();
					}).delay(500);
				}
			},
			url : ps['url'],
			jsCssFiles : ps['jsCssFiles'],
			jsCssFileMgr : this.options['jsCssFileMgr']
		});
	}
});
var UIEditWindow = new Class({
	Extends : UITitleWindow,
	options : {
		'buttonPanelParams' : {
			'class' : 'buttonpanel'
		},
		'promptParams' : {
			'class' : 'prompt',
			'error_class' : 'prompt_error'
		},
		'buttons' : [ {
			id : 'save',
			'params' : {
				labelParams : {
					text : '保存'
				}
			}
		}, {
			id : 'cancel',
			'params' : {
				labelParams : {
					text : '取消'
				}
			}
		} ]
	},
	Extends : UITitleWindow,
	doCreate : function(options) {
		this.parent(options);
		this.createButtonPanel(options);
	},
	buttonClick : function(b) {
		switch (b.id) {
		case 'cancel':
			this.hide();
			break;
		case 'save':
			this.save();
			break;
		}
	},
	save : function(url) {
		if (this.$loading) {
			alert('正在装入，请稍候...');
			return;
		}
		if (this.saving)
			return;
		this.saving = true;
		var self = this;
		var ps = this.options['promptParams'];
		var p = this.prompt;
		if (typeOf(this.validate) == 'function')
			if (!this.validate()) {
				this.saving = false;
				return;
			}
		var form = this.content.getElements('input')[0].form;
		var doError = function(c) {
			if (p) {
				p.addRemoveClass(ps['error_class'], ps['class']);
				p.set('text', '保存失败:' + c.errMsg);
			}
			self.saving = false;
		};
		var doSuccess = function(r) {
			if (p)
				p.set('text', '');
			if (self.fireEvent('saveComplete', r))
				self.hide();
		};
		if (form.enctype != 'multipart/form-data' || window.FormData) {
			var data, m = 'post';
			if (form.enctype != 'multipart/form-data') {
				data = form.toQueryString();
				m = 'get';
			} else {
				data = new FormData(form);
			}
			new JsonRequest({
				urlEncoded : false,
				url : url ? url : this.options['save_url'],
				method : m,
				onError : function(c) {
					doError(c);
				},
				onSuccess : function(r) {
					doSuccess(r);
				},
				'onUploadProgress' : function(e) {
					console.log(e);
				}
			}).send({
				'data' : data
			});
		} else {
			form.action = contextPath + (url ? url : this.options['save_url']);
			form.method = 'post';
			var id = String.uniqueID();
			form.target = id;
			var iframe = new Element('iframe', {
				'id' : id
			}).inject(document.body).setStyle('display', 'none').addEvent('load', function() {
				var result = JSON.decode(this.contentDocument.body.innerText);
				iframe.dispose();
				if (!result.result.success) {
					doError({
						errMsg : result.result.message
					});
				} else {
					doSuccess({
						'data' : result
					});
				}
			});
			form.submit();
		}
		if (p) {
			p.addRemoveClass(ps['class'], ps['error_class']);
			p.set('text', '正在保存...');
		}
	},
	createButtonPanel : function(options) {
		var s = options['buttons'], self = this;
		this.buttonPanel = new Element('div', options['buttonPanelParams']).inject(this.content, 'after');
		this.buttons = {};
		for (var i = 0; i < s.length; i++) {
			var b = this.buttons[s[i].id] = new UIButton(Object.merge(s[i].params, {
				'parent' : this.buttonPanel,
				'events' : {
					'click' : function() {
						self.buttonClick(this);
					}
				}
			}));
			b.id = s[i].id;
		}
		this.prompt = new Element('span', options['promptParams']['class']).inject(this.buttonPanel, 'top');
	}
});
function createInput(options) {
	var type = options.type || 'text';
	var id = options['id'];
	var o = {
		parent : options['parent'],
		panel : options['panel'],
		'createParams' : {},
		'events' : {},
		'inputParams' : {
			'readonly' : options['readonly']
		}
	};
	if (options['class'])
		o['createParams']['class'] = options['class'];
	if (options['classPrefix'])
		o['classPrefix'] = options['classPrefix'];
	if (options['buttonClick'])
		o['events']['buttonClick'] = options['buttonClick'];
	switch (type) {
	case 'search':
		return new UIComboInput(Object.merge({
			'createParams' : {
				'class' : 'inline_block k_search_input k_input'
			},
			'buttonParams' : {
				'labelParams' : {
					'class' : 'search_btn_l'
				}
			},
			'inputParams' : {
				'type' : 'text',
				'id' : id,
				'name' : id,
				'placeholder' : options['placeholder'],
				'value' : options['value'],
				'readonly' : options['readonly']
			}
		}, o));
	case 'text':
	case 'password':
	case 'file':
		return new UIInput(Object.merge({
			'inputParams' : {
				'type' : type,
				'id' : id,
				'name' : id,
				'placeholder' : options['placeholder'],
				'value' : type == 'text' ? options['value'] : '',
				'readonly' : options['readonly'] ? 'readonly' : '',
			}
		}, o));
	case 'list':
		return new UIListControl(Object.merge({
			'items' : options['items'],
			'input' : id,
			'multiselect' : options['multiselect'],
			'requestParams' : options['requestParams'] || {},
			'searchInputParams' : options['searchInputParams'],
			'nextButtonParams' : options['nextButtonParams'],
			'value' : options['value']
		}, o));
	case 'icon_list':
		return new UIListControl(Object.merge({
			'listItemParams' : {
				'createParams' : {
					'class' : 'k_icon_listitem k_listitem'
				},
				'iconParams' : {}
			},
			'items' : options['items'],
			'input' : id,
			'multiselect' : options['multiselect'],
			'requestParams' : options['requestParams'] || {},
			'searchInputParams' : options['searchInputParams'],
			'nextButtonParams' : options['nextButtonParams'],
			'value' : options['value']
		}, o));
	case 'combobox':
	case 'combo':
		return new UIComboBox(Object.merge({
			'input' : id,
			'list' : {
				'items' : options['items'],
				'multiselect' : options['multiselect'],
				'requestParams' : options['requestParams'] || {},
				'searchInputParams' : options['searchInputParams'],
				'nextButtonParams' : options['nextButtonParams'],
				'value' : options['value'],
				initload : true
			},
			'inputParams' : {
				'readonly' : options['readonly']
			},
			'autocomplete' : options['autocomplete']
		}, o));
	case 'chosenbox':
	case 'chosenbox_search':
		return new UIChosenBox(Object.merge({
			'input' : id,
			'list' : {
				'items' : options['items'],
				'multiselect' : options['multiselect'],
				'requestParams' : options['requestParams'] || {},
				'searchInputParams' : type == 'chosenbox_search' ? {} : undefined,
				'nextButtonParams' : {},
				initload : true
			},
			'inputParams' : {
				'readonly' : options['readonly']
			},
			'value' : options['value'],
			'autocomplete' : options['autocomplete']
		}, o));
	case 'datetime':
	case 'date':
		return new UICalendar(Object.merge({
			'input' : id,
			'timeselectenabled' : type == 'datetime',
			'startdate' : options['minvalue'] || '1900-01-01',
			'enddate' : options['maxvalue'] || '2200-01-01',
			'value' : options['value']
		}, o));
	case 'datetimebox':
	case 'datebox':
		return new UICalendarBox(Object.merge({
			'input' : id,
			'calendar' : {
				'timeselectenabled' : type == 'datetimebox',
				'startdate' : options['minvalue'] || '1900-01-01',
				'enddate' : options['maxvalue'] || '2200-01-01',
				'value' : options['value']
			}
		}, o));
	case 'tree':
		return new UITreeControl(Object.merge({
			'createParams' : {
				'class' : 'inline_block k_input'
			},
			'listPanelParams' : {
				'class' : 'k_tree edit_tree'
			},
			'listItemParams' : {
				'createParams' : {
					'class' : options['checkboxes'] ? 'k_treeitem_checkbox' : 'k_treeitem'
				},
				'classPrefix' : options['checkboxes'] ? 'k_treeitem_checkbox' : 'k_treeitem'
			},
			'input' : id,
			'items' : options['items'],
			'requestParams' : options['requestParams'] || {},
			'value' : options['value'],
			'multiselect' : options['multiselect'],
			'checkboxes' : options['checkboxes']
		}, o));
	case 'treebox': {
		return new UITreeBox(Object.merge({
			'input' : id,
			tree : {
				'listItemParams' : {
					'createParams' : {
						'class' : options['checkboxes'] ? 'k_treeitem_checkbox' : 'k_treeitem'
					},
					'classPrefix' : options['checkboxes'] ? 'k_treeitem_checkbox' : 'k_treeitem'
				},
				'items' : options['items'],
				'requestParams' : options['requestParams'] || {},
				'value' : options['value'],
				'multiselect' : options['multiselect'],
				'checkboxes' : options['checkboxes']
			}
		}, o));
	}
	}
	return null;
}
function $input(options) {
	var r = createInput(options), o = $(options['id']);
	if (o) {
		if (options['type'] == 'file') {
			var form = o.form;
			if (form.enctype != 'multipart/form-data') {
				form.enctype = "multipart/form-data";
			}
		}
		o.set('is_validate_input', true);
		o.set('prompt', options['prompt']);
		o.set('errorprompt', options['errorprompt']);
		o.set('normalprompt', options['normalprompt']);
		o.set('regexp', options['regexp']);
		o.set('maxvalue', options['maxvalue']);
		o.set('minvalue', options['minvalue']);
		o.set('maxlength', options['maxlength']);
		if (options['isadd'] || options['type'] != 'password')
			o.set('minlength', options['minlength']);
	}
	return r;
}
