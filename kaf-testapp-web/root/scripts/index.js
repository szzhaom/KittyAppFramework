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

function createInput(options) {
	var type = options.type || 'text';
	var id = options['id'];
	var o = {
		parent : options['parent'],
		panel : options['panel'],
		'createParams' : {},
		'events' : {}
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
		return new UIInput(Object.merge({
			'inputParams' : {
				'type' : type,
				'id' : id,
				'name' : id,
				'placeholder' : options['placeholder'],
				'value' : type == 'text' ? options['value'] : '',
				'readonly' : options['readonly'] ? 'readonly' : '',
				'maxlength' : options['maxlength']
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
			'startdate' : options['minvalue'],
			'enddate' : options['maxvalue'],
			'value' : options['value']
		}, o));
	case 'datetimebox':
	case 'datebox':
		return new UICalendarBox(Object.merge({
			'input' : id,
			'calendar' : {
				'timeselectenabled' : type == 'datetimebox',
				'startdate' : options['minvalue'],
				'enddate' : options['maxvalue'],
				'value' : options['value']
			}
		}, o));
	case 'tree':
		return new UITreeControl(Object.merge({
			'createParams' : {
				'class' : 'inline_block k_input'
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
	}
	return null;
}
function $input(options) {
	var r = createInput(options), o = $(options['id']);
	if (o) {
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