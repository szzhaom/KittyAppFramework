(function() {
	var arrayCombine = function(s, prefix, sp, from, dest) {
		if (prefix.length > 0)
			dest.push(prefix);
		for ( var i = from; i < s.length; i++) {
			if (prefix.length > 0 && !prefix.endsWith(sp))
				prefix += sp;
			arrayCombine(s, prefix + s[i], sp, i + 1, dest);
		}
	};
	String.implement({
		replaceAll : function(o, n) {
			return this.replace(new RegExp(o, 'gm'), n);
		},
		ltrim : function() {
			return this.replace(/(^\s*)/g, "");
		},
		rtrim : function() {
			return this.replace(/(\s*$)/g, "");
		},
		getBytesLength : function() {
			return this.replace(/[^\x00-\xff]/g, "**").length;
		},
		startsWith : function(s) {
			return this.indexOf(s) == 0;
		},
		endsWith : function(s) {
			return this.lastIndexOf(s) == this.length - s.length;
		},
		toDate : function() {
			var time = Date.parse(this);
			if (isNaN(time)) {
				time = Date.parse(this.replace(/-/g, "/"));
				if (isNaN(time)) {
					time = 0;
				}
			}
			return new Date(time);
		},
		toInt : function(def) {
			var s = parseInt(this.replace(/0*(\d+)/, "$1"));
			if (typeOf(s) != 'number')
				s = def ? def : 0;
			return s;
		}
	});
	Array.implement({
		earseAll : function(items) {
			for ( var i = 0; i < items.length; i++)
				this.erase(items[i]);
			return this;
		},
		toString : function() {
			var r = '';
			for ( var i = 0; i < this.length; i++) {
				if (r.length > 0)
					r += ',';
				r += this[i];
			}
			return r;
		},
		combineArray : function(p) {
			var r = [];
			arrayCombine(this, '', p || '_', 0, r);
			return r;
		}
	});
})();
/**
 * 扩展元素的能力
 */
(function() {
	var sce = function(e) {
		e.target = this.$captureObject;
		this.$captureObject.fireEvent('mouseup', e);
	};
	var rf = function(o) {
		return o.toFloat().round();
	};
	var gss = function() {
		var o = new Element('div', {
			'styles' : {
				'width' : '100px',
				'height' : '100px',
				'overflow' : 'scroll',
				'position' : 'absolute',
				'top' : '-200px',
				'left' : '-200px'
			}
		}).inject(document.documentElement, 'top');
		var v = o.offsetWidth - o.clientWidth;
		o.destroy();
		return v;
	};
	var pf = function(s) {
		var a = ((typeOf(s) == 'string') ? s : '0px 0px 0px 0px').split(' ');
		if (a.length == 1) {
			a[1] = a[2] = a[3] = a[0];
		} else if (a.length == 2) {
			a[2] = a[0];
			a[3] = a[1];
		}
		return {
			top : rf(a[0]),
			right : rf(a[1]),
			bottom : rf(a[2]),
			left : rf(a[3]),
			hor : rf(a[1]) + rf(a[3]),
			ver : rf(a[2]) + rf(a[0])
		};
	};
	Element.placeHolderSupported = 'placeholder' in new Element('input');
	Element.scrollBarWidth = gss();
	Element.implement({
		setBounds : function(bounds) {
			if (bounds['width'] != undefined)
				this.setStyle('width', bounds['width'] + 'px');
			if (bounds['height'] != undefined)
				this.setStyle('height', bounds['height'] + 'px');
			if (bounds['left'] != undefined)
				this.setStyle('left', bounds['left'] + 'px');
			if (bounds['top'] != undefined)
				this.setStyle('top', bounds['top'] + 'px');
		},
		getBorders : function() {
			return pf(this.getStyle('border-width'));
		},
		getPaddings : function() {
			return pf(this.getStyle('padding'));
		},
		getMargins : function() {
			return pf(this.getStyle('margin'));
		},
		getComputedSize : function() {
			var r = this.getSize();
			r.borders = this.getBorders();
			r.paddings = this.getPaddings();
			r.margins = this.getMargins();
			r.totalWidth = r.x + r.margins.hor;
			r.totalHeight = r.y + r.margins.ver;
			r.hor = r.borders.hor + r.paddings.hor + r.margins.hor;
			r.ver = r.borders.ver + r.paddings.ver + r.margins.ver;
			r.width = r.x - r.borders.hor - r.paddings.hor;
			r.height = r.y - r.borders.ver - r.paddings.ver;
			r.computedTop = r.borders.top + r.paddings.top + r.margins.top;
			r.computedBottom = r.borders.bottom + r.paddings.bottom + r.margins.bottom;
			r.computedLeft = r.borders.left + r.paddings.left + r.margins.left;
			r.computedRight = r.borders.right + r.paddings.right + r.margins.right;
			delete r.x;
			delete r.y;
			return r;
		},
		startCapture : function() {
			if (this.setCapture)
				this.setCapture();
			else {
				document.documentElement.$captureObject = this;
				document.documentElement.addEvent('mouseup', sce);
			}
		},
		stopCapture : function() {
			if (this.releaseCapture)
				this.releaseCapture();
			else if (document.documentElement.$captureObject == this) {
				delete document.documentElement.$captureObject;
				document.documentElement.removeEvent('mouseup', sce);
			}
		},
		setSelectRange : function(s, e) {
			if (this.setSelectionRange) {
				this.focus();
				this.setSelectionRange(s, e);
			} else if (this.createTextRange) {
				var range = this.createTextRange();
				range.collapse(true);
				range.moveEnd('character', e);
				range.moveStart('character', s);
				range.select();
			}
		},
		getSelectStart : function() {
			if (this.selectionStart != undefined)
				return this.selectionStart;
			else {
				var range = document.selection.createRange();
				range.moveStart('character', -this.get('value').length);
				return range.text.length;
			}
		},
		addRemoveClass : function(a, r) {
			if (typeOf(a) == 'array') {
				for ( var i = 0; i < a.length; i++)
					this.addClass(a[i]);
			} else
				this.addClass(a);
			if (typeOf(r) == 'array') {
				for ( var i = 0; i < r.length; i++)
					this.removeClass(r[i]);
			} else
				this.removeClass(r);
		},
		setSelectable : function(v) {
			this.set('unselectable', !v ? 'on' : 'off');
			this.setStyle('-moz-user-select', !v ? 'none' : '');
			this.setStyle('-webkit-user-select', !v ? 'none' : '');
			this.set('onselectstart', 'return ' + v + ';');
			this.set('ondragstart', 'return ' + v + ';');
			return this;
		},
		getViewBounds : function() {
			var sc = this.getScroll(), size = this.getSize();
			return {
				'left' : sc.x,
				'top' : sc.y,
				'width' : size.x,
				'height' : size.y,
				'bottom' : sc.y + size.y,
				'right' : sc.x + size.x
			};
		},
		isParent : function(child) {
			var p = child;
			while (p != null) {
				if (p == this)
					return true;
				p = p.getParent();
			}
			return false;
		},
		isVisible : function() {
			return this.getParent() != null && this.getStyle('display') != 'none';
		}
	});
})();
var UIControl = new Class({
	Implements : [ Events, Options ],
	status : {
		'over' : false
	},
	availableStatus : [ 'over' ],
	options : {
		'createParams' : {}
	},
	initialize : function(options) {
		this.setOptions(options);
		if (this.options['events']) {
			this.addEvents(this.options['events']);
			delete this.options['events'];
		}
		this.create();
	},
	doCreate : function(options) {
		var c = options['createParams'];
		this.panel = $(options['panel']);
		if (!this.panel) {
			this.panel = new Element('div', c);
			if ($(options['parent']))
				this.inject($(options['parent']));
		} else if (c['class'])
			this.panel.addClass(c['class']);
		var self = this;
		this.panel.addEvents({
			'mouseover' : function() {
				self.setStatus('over', true);
			},
			'mouseout' : function() {
				self.setStatus('over', false);
			}
		});
	},
	create : function() {
		this.doCreate(this.options);
		this.fireEvent('created');
		return this;
	},
	inject : function(d, p) {
		this.panel.inject(d, p);
		return this;
	},
	destroy : function() {
		this.panel.destroy();
		delete this.panel;
	},
	getInput : function() {
		if (!this.input) {
			var id = this.options['input'];
			var input = $(id);
			if (!input && typeOf(id) == 'string') {
				input = new Element('input', {
					'type' : 'hidden',
					'id' : id,
					'name' : id
				}).inject(this.panel);
			}
			this.input = input;
		}
		return this.input;
	},
	isVisible : function() {
		return this.panel && this.panel.isVisible();
	},
	setValue : function(v) {
	},
	getValue : function() {
		return '';
	},
	setStatus : function(p, v) {
		if (this.status[p] == v)
			return;
		this.status[p] = v;
		this.statusChanged(p);
	},
	getStatus : function(p) {
		return this.status[p];
	},
	getComputedStatus : function() {
		var s = this.status;
		if (s['over'] || s['parent_over'])
			return 'over';
		else
			return '';
	},
	statusChanged : function(s) {
		this.fireEvent('statuschanged', s);
	},
	createWindow : function(options) {
		if (instanceOf(options['window'], UIWindow))
			this.window = options['window'];
		else {
			var p = options['windowParams'];
			p['bounds']['target']['element'] = this.panel;
			this.window = new UIWindow(options['windowParams']);
		}
	},
	addWindowEvents : function() {
		var self = this;
		this.window.addEvents({
			'effect' : function(e) {
				var s = Object.clone(e['bounds']), panel = this.panel;
				if (this.popupSide == 'top')
					s.top = e['bounds'].top + e['bounds'].height;
				s.height = 0;
				s['overflow'] = 'hidden';
				var saved = panel.getStyle('overflow');
				this.panel.setStyles(s);
				panel.set('morph', {
					duration : 200
				});
				var morph = panel.get('morph'), complete = null;
				complete = function() {
					panel.setStyle('overflow', saved);
					morph.removeEvent('complete', complete);
				};
				morph.addEvent('complete', complete);
				this.panel.morph(e['bounds']);
				delete e['bounds'];
			},
			'show' : function() {
				self.windowShow.call(self);
			},
			'hide' : function() {
				self.windowHide.call(self);
			}
		});
	}
});
var UIWindow = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'k_window',
			'styles' : {
				'position' : 'absolute'
			}
		},
		'contentParams' : {
			'class' : 'content'
		},
		'bounds' : {
			'target' : undefined,
			'postion' : undefined,
			'range' : {
				'maxHeight' : 0,
				'minHeight' : 0,
				'maxWidth' : 0,
				'minHeight' : 0
			}
		}
	},
	doCreate : function(options) {
		this.parent(options);
		var self = this;
		this.panel.addEvents({
			'scroll' : function() {
				self.scrollChange();
			}
		});
		this.content = this.createContent(options).inject(this.panel);
	},
	createContent : function(options) {
		if ($(options['content']))
			return $(options['content']);
		else
			return new Element('div', options['contentParams']);
	},
	isScrollDisabled : function() {
		return this.scrollDisabld;
	},
	scrollChange : function() {
		var s = this.panel.getScroll(), os = this.savedScroll;
		if (!os)
			os = {
				x : 0,
				y : 0
			};
		if (os && this.isScrollDisabled()) {
			if (s.x != os.x || s.y != os.y) {
				this.panel.scrollTo(os.x, os.y);
			}
			return;
		}
		this.savedScroll = s;
	},
	isShowing : function() {
		return this.panel.getParent();
	},
	show : function() {
		if (!this.isShowing()) {
			this.bringToFront();
			this.doShow();
			this.fireEvent('show');
		}
		return this;
	},
	bringToFront : function() {
		this.panel.inject($(document.body).getLast(), 'after');
	},
	doShow : function() {
		this.content.setStyle("display", "block");
		this.panel.setStyle("display", "block");
		this.resetBounds();
		var t = this.options.bounds.target;
		if (t != undefined && typeOf(t['element']) == 'element') {
			var self = this;
			var f = this.$pmousedown = function(e) {
				if (!(self.panel.isParent(e.target) || $(t['element']).isParent(e.target))) {
					self.hide();
				}
			};
			document.documentElement.addEvent('mousedown', f);
		}
	},
	runEffect : function(bounds) {
		var e = {
			'bounds' : bounds
		};
		this.fireEvent('effect', e);
		if (e['bounds'])
			this.panel.setBounds(e['bounds']);
	},
	resetBounds : function(notEffect) {
		this.panel.setStyle('width', '');
		this.panel.setStyle('height', '');
		var r = this.getBounds();
		if (notEffect)
			this.panel.setBounds(r);
		else
			this.runEffect(r);
	},
	getBounds : function() {
		var bounds = this.options['bounds'], target = bounds['target'], range = bounds['range'];
		var r = this.panel.getComputedSize(), w = r.totalWidth, h = r.totalHeight;
		var bodyView = $(document.body).getViewBounds();
		if (range['maxWidth'] > 0 && w > range['maxWidth'])
			w = range['maxWidth'];
		if (range['minWidth'] > 0 && w < range['minWidth'])
			w = range['minWidth'];
		if (range['maxHeight'] > 0 && h > range['maxHeight']) {
			h = range['maxHeight'];
			w += Element.scrollBarWidth;
		}
		if (range['minHeight'] > 0 && h < range['minHeight'])
			h = range['minHeight'];
		var pos = bounds['position'];
		if (pos == undefined && target != undefined) {
			pos = $(target['element']).getPosition();
			var size = $(target['element']).getSize();
			if (target['popupside'] == 'right') {
				pos.x += size.x;
				this.popupSide = 'right';
				if (pos.x + w > bodyView.right && pos.x - size.x - w >= bodyView.left) {
					pos.x -= size.x + w;
					this.popupSide = 'left';
				}
				switch (target['align']) {
				case 'bottom':
					pos.y -= h - size.y;
					break;
				case 'middle':
					pos.y -= (h - size.y) / 2;
					break;
				}
				if (pos.y + h > bodyView.bottom)
					pos.y = bodyView.bottom - h;
				if (pos.y < bodyView.top)
					pos.y = bodyView.top;
			} else {
				pos.y += size.y;
				if (w < size.x)
					w = size.x;
				this.popupSide = 'bottom';
				if (pos.y + h > bodyView.bottom && pos.y - size.y - h >= bodyView.top) {
					pos.y -= size.y + h;
					this.popupSide = 'top';
				}
				switch (target['align']) {
				case 'right':
					pos.x -= w - size.x;
					break;
				case 'center':
					pos.x -= (w - size.x) / 2;
					break;
				}
				if (pos.x + w > bodyView.right)
					pos.x = bodyView.right - w;
				if (pos.x < bodyView.left)
					pos.x = bodyView.left;
			}
			pos.x += target['offsetx'] || 0;
			pos.y += target['offsety'] || 0;
		}
		if (pos == undefined) {
			pos = {
				'x' : (bodyView.width - w) / 2,
				'y' : (bodyView.height - h) / 2
			};
		}
		return {
			'left' : pos.x + bodyView.left,
			'top' : pos.y + bodyView.top,
			'width' : w - r.hor,
			'height' : h - r.ver
		};
	},
	doHide : function() {
		this.panel.dispose();
		if (this.$pmousedown) {
			document.documentElement.removeEvent('mousedown', this.$pmousedown);
			delete this.$pmousedown;
		}
	},
	hide : function() {
		if (this.isShowing()) {
			this.doHide();
			this.fireEvent('hide');
		}
	}
});
var UIModelWindow = new Class({
	Extends : UIWindow,
	options : {
		'mask' : {
			'styles' : {
				'opacity' : 0.5,
				'background' : '#ffffff',
				'position' : 'absolute'
			}
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.mask = new Element('div', options['mask']);
	},
	bringToFront : function() {
		this.parent();
		this.mask.inject(this.panel, 'before');
	},
	resetBounds : function() {
		this.parent();
		this.mask.setBounds($(document.body).getViewBounds());
	},
	doHide : function() {
		this.parent();
		this.mask.dispose();
	}
});
var UIInput = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'inline_block k_input'
		},
		'inputParams' : {
			'type' : 'text',
			'class' : 'input'
		},
		'placeholderParams' : {
			'labelClass' : 'placeholder'
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.createInput(options['inputParams']);
		if (options['windowParams'] != undefined) {
			this.createWindow(options);
			this.addWindowEvents();
		}
	},
	createInput : function(options) {
		var self = this;
		this.textInput = new Element('input', options).inject(this.panel).addEvents({
			'click' : function(e) {
				self.click();
			}
		});
		if (options['value'])
			this.setValue(options['value']);
		if (options['placeholder'])
			this.doPlaceHolder(options['placeholder']);
	},
	click : function() {
		if (this.window) {
			if (this.window.isShowing())
				this.window.hide();
			else
				this.window.show();
		}
	},
	setValue : function(v) {
		this.textInput.set('value', v);
	},
	getValue : function() {
		return this.textInput.get('value');
	},
	doPlaceHolder : function(placeHolder) {
		if (Element.placeHolderSupported)
			return;
		var self = this, input = this.textInput;
		this.placeHolder = new Element('label', {
			'class' : this.options['placeholderParams']['labelClass'],
			'text' : placeHolder,
			'events' : {
				'mousedown' : function(e) {
					e.stop();
					input.focus();
				}
			}
		}).inject(this.input, 'before');
		input.addEvents({
			'keydown' : function(e) {
				self.keyDown.call(self, e);
			},
			'keyup' : function(e) {
				self.keyUp.call(self, e);
			}
		});
	},
	keyUp : function(e) {
		var p = this.placeHolder;
		if (UIInput.isCharKeyEvent(e) && input.get('value') == '')
			p.setStyle('display', 'none');
	},
	keyDown : function(e) {
		var p = this.placeHolder;
		if (this.textInput.get('value') != '') {
			p.setStyle('display', 'none');
		} else
			p.setStyle('display', '');
	},
	windowShow : function() {
	},
	windowHide : function() {
	}
});
UIInput.isCharKeyEvent = function(e) {
	var c = e.code;
	return (c >= 65 && c <= 90) || (c >= 48 && c <= 57) || (c >= 186 && c <= 192) || (c >= 219 && c <= 222) || c == 32
			|| (c >= 96 && c <= 111);
};
UIButton = new Class({
	Extends : UIControl,
	status : {
		'down' : false,
		'over' : false
	},
	availableStatus : [ 'down', 'over', 'over_down' ],
	options : {
		'createParams' : {
			'class' : 'inline_block k_button'
		},
		'type' : 'button',
		'labelParams' : {
			'class' : 'inline_block label'
		},
		'classPrefix' : 'k_button',
		'stopMouseEvents' : false,
		'mouseDownClicks' : false
	},
	createContent : function(options) {
		this.captionLabel = new Element('div', options['labelParams']).inject(this.panel).setSelectable(false);
	},
	doCreate : function(options) {
		this.parent(options);
		this.createContent(options);
		var self = this;
		this.panel.addEvents({
			'mouseover' : function() {
				self.setStatus('over', true);
			},
			'mouseout' : function() {
				self.setStatus('over', false);
			},
			'mousedown' : function(e) {
				if (self.options.stopMouseEvents)
					e.stop();
				if (e.rightClick)
					return;
				self.panel.startCapture();
				self.setStatus('down', true);
				if (options['mouseDownClicks']) {
					self.$mtimer = setInterval(function() {
						self.$mousedownclicked = true;
						self.click();
					}, 150);
				}
			},
			'mouseup' : function(e) {
				if (self.options.stopMouseEvents)
					e.stop();
				if (e.rightClick)
					return;
				clearInterval(self.$mtimer);
				delete self.$mtimer;
				self.panel.stopCapture();
				self.setStatus('down', false);
			},
			'click' : function(e) {
				if (self.options.stopMouseEvents)
					e.stop();
				if (!self.$mousedownclicked) {
					self.click();
				} else
					delete self.$mousedownclicked;
			}
		});
	},
	setCaption : function(v) {
		this.captionLabel.set('text', v);
		this.captionLabel.set('title', v);
	},
	getCaption : function() {
		return this.captionLabel.get('text');
	},
	getComputedStatus : function() {
		var s = this.status;
		if (s['down'] || s['check']) {
			if (s['over'] || s['parent_over'])
				return 'over_down';
			else
				return 'down';
		} else
			return this.parent();
	},
	statusChanged : function(s) {
		var d = this.options['classPrefix'];
		var a = this.getComputedStatus(), r = [], as = this.availableStatus;
		for ( var i = 0; i < as.length; i++) {
			if (a != as[i])
				r.push(d + '_' + as[i]);
		}
		this.panel.addRemoveClass(a.length > 0 ? d + '_' + a : '', r);
		this.parent(s);
	},
	click : function() {
		switch (this.options['type']) {
		case 'radio':
			this.setStatus('check', true);
			break;
		case 'check':
			this.setStatus('check', !this.status['check']);
			break;
		}
		this.fireEvent('click');
	}
});
var UIComboInput = new Class({
	Extends : UIInput,
	options : {
		'buttonParams' : {
			'createParams' : {
				'class' : 'drop_btn'
			},
			'labelParams' : {
				'class' : 'drop_btn_l'
			},
			'classPrefix' : 'drop_btn',
			'stopMouseEvents' : true
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.createButton(options);
	},
	createButton : function(options) {
		var self = this;
		this.button = new UIButton(options['buttonParams']).inject(this.panel).addEvent('click', function() {
			self.buttonClick.call(self);
		});
	},
	buttonClick : function() {
		this.fireEvent('buttonClick');
	},
	statusChanged : function(s) {
		if (s == 'over' && this.button)
			this.button.setStatus('parent_over', this.status['over']);
		this.parent(s);
	}
});
var UIComboButton = new Class({
	Extends : UIButton,
	options : {
		'createParams' : {
			'class' : 'inline_block k_combo_btn'
		},
		'rightButtonParams' : {
			'createParams' : {
				'class' : 'right_btn'
			},
			'labelParams' : {
				'class' : 'right_btn_l'
			},
			'classPrefix' : 'right_btn',
			'stopMouseEvents' : true
		},
		'classPrefix' : 'k_combo_btn'
	},
	createContent : function(options) {
		this.parent(options);
		this.createRightButton(options);
	},
	createRightButton : function(options) {
		var self = this;
		this.rightButton = new UIButton(options['rightButtonParams']).inject(this.panel).addEvent('click', function() {
			self.rightButtonClick.call(self);
		});
	},
	rightButtonClick : function() {
		this.fireEvent('rightButtonClick');
	},
	statusChanged : function(s) {
		if (s == 'over' && this.rightButton)
			this.rightButton.setStatus('parent_over', this.status['over']);
		this.parent(s);
	}
});
var UIDropdownButton = new Class({
	Extends : UIComboButton,
	options : {
		'windowParams' : {
			'bounds' : {
				'target' : {}
			}
		},
		'clickDropdown' : true
	},
	doCreate : function(options) {
		this.parent(options);
		this.createWindow(options);
		this.addWindowEvents();
	},
	rightButtonClick : function() {
		this.parent();
		if (this.window.isShowing())
			this.window.hide();
		else
			this.window.show();
	},
	click : function() {
		if (this.options['clickDropdown'])
			this.rightButtonClick();
		this.parent();
	},
	dropdown : function() {
		this.window.show();
	},
	collapse : function() {
		this.window.hide();
	},
	windowShow : function() {
		if (this.options['clickDropdown'])
			this.setStatus('check', true);
		if (this.rightButton)
			this.rightButton.setStatus('check', true);
	},
	windowHide : function() {
		if (this.options['clickDropdown'])
			this.setStatus('check', false);
		if (this.rightButton)
			this.rightButton.setStatus('check', false);
	}
});
var UIListItem = new Class({
	Extends : UIButton,
	options : {
		'type' : 'radio',
		'createParams' : {
			'class' : 'k_listitem'
		},
		'classPrefix' : 'k_listitem'
	},
	createContent : function(options) {
		options['labelParams']['text'] = options['data']['text'];
		this.parent(options);
	},
	getText : function() {
		return this.options['data']['text'];
	},
	getValue : function() {
		var o = this.options['data'];
		return o['value'] || o['id'];
	},
	doCreate : function(options) {
		this.parent(options);
		this.input = new Element('input', {
			'type' : 'hidden'
		}).set('value', options['data']['value']);
	},
	statusChanged : function(s) {
		this.parent();
		if (s == 'check') {
			if (this.options['type'] == 'radio' && this.status['check']) {
				var items = this.list.items;
				for ( var i = 0; i < items.length; i++) {
					if (items[i] != this)
						items[i].setStatus('check', false);
				}
			}
			this.list.updateInput();
		}
	},
	isVisible : function() {
		return true;
	}
});
var UIListControl = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'inline_block k_list'
		},
		'listItemParams' : {
			'events' : {}
		},
		'emptyParams' : {
			'class' : 'empty_item'
		},
		'multiselect' : false,
		'listItemClass' : UIListItem
	},
	items : [],
	createEmptyItem : function(options) {
		if (options['emptyParams']['text'] != undefined) {
			this.emptyItem = new Element('div', options['emptyParams']).inject(this.panel).setStyle('display', 'none');
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.getInput();
		this.resetItems(options['items']);
		if (options['value'] != undefined)
			this.setValue(options['value']);
		this.createEmptyItem(options);
		this.visibleItemsChange();
	},
	clearItems : function() {
		for ( var i = 0; i < this.items.length; i++) {
			this.items.destroy();
		}
		this.items = [];
	},
	find : function(o) {
		for ( var i = 0; i < this.items.length; i++) {
			var d = this.items[i]['options']['data'], v = d['value'] || d['id'];
			if (v == o)
				return this.items[i];
		}
		return null;
	},
	updateInput : function() {
		if (this.$notUpdateInput)
			return;
		var v = this.getValue();
		if (this.$value != v) {
			this.$value = v;
			if (this.input)
				this.input.set('value', v);
			this.fireEvent('valueChanged');
		}
	},
	getSelItems : function() {
		var s = [];
		for ( var i = 0; i < this.items.length; i++) {
			if (this.items[i].status['check']) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getUnselItems : function() {
		var s = [];
		for ( var i = 0; i < this.items.length; i++) {
			if (!this.items[i].status['check']) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getVisibleItems : function() {
		var s = [];
		for ( var i = 0; i < this.items.length; i++) {
			if (this.items[i].isVisible()) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getValue : function() {
		var r = '';
		for ( var i = 0; i < this.items.length; i++) {
			if (this.items[i].status['check']) {
				if (r.length > 0)
					r += ',';
				r += this.items[i].getValue();
			}
		}
		return r;
	},
	getText : function() {
		var r = '';
		for ( var i = 0; i < this.items.length; i++) {
			if (this.items[i].status['check']) {
				if (r.length > 0)
					r += ',';
				r += this.items[i].getText();
			}
		}
		return r;
	},
	setValue : function(v) {
		this.$notUpdateInput = true;
		if (typeOf(v) == 'string')
			v = v.split(',');
		for ( var i = 0; i < v.length; i++) {
			var o = this.find(v[i]);
			if (o)
				o.setStatus('check', true);
		}
		delete this.$notUpdateInput;
		this.updateInput();
	},
	resetItems : function(items) {
		this.clearItems();
		for ( var i = 0; i < items.length; i++) {
			var p = Object.clone(this.options['listItemParams']);
			p['data'] = items[i];
			p['isRadio'] = !this.options['multiselect'];
			var r = this.createItem(p), self = this;
			r.list = this;
			r.addEvent('click', function() {
				self.itemClick.call(self, this);
			});
			this.items.push(r);
		}
		this.visibleItemsChange();
	},
	createItem : function(options) {
		return new this.options['listItemClass'](options).inject(this.panel);
	},
	itemClick : function(item) {
		this.fireEvent('itemClick', item);
	},
	visibleItemsChange : function() {
		var s = this.getVisibleItems();
		if (this.emptyItem)
			this.emptyItem.setStyle('display', s.length > 0 ? 'none' : '');
	}
});
var UIComboBox = new Class({
	Extends : UIDropdownButton,
	options : {
		'list' : {}
	},
	create : function(options) {
		this.parent(options);
		this.setCaption(this.list.getText());
	},
	createWindow : function(options) {
		var listOptions = Object.clone(options['list']);
		var panel = listOptions['panel'] = new Element('div'), self = this;
		listOptions['input'] = this.getInput();
		this.list = new UIListControl(listOptions);
		this.list.addEvents({
			'itemClick' : function(item) {
				self.itemClick.call(self, item);
			},
			'valueChanged' : function() {
				self.valueChanged.call(self);
			}
		});
		var p = options['windowParams'];
		p['content'] = panel;
		p['bounds']['target']['element'] = this.panel;
		this.window = new UIWindow(options['windowParams']);
	},
	itemClick : function(item) {
		if (!this.options['list']['multiselect']) {
			this.selected = item;
			this.setCaption(item.getText());
			item.setStatus('over', false);
			this.collapse();
		}
		this.fireEvent('itemClck', item);
	},
	valueChanged : function() {
		this.setCaption(this.list.getText());
		this.fireEvent('valueChanged');
	},
	windowShow : function() {
		this.parent();
		var s = this.list.items;
		for ( var i = 0; i < s.length; i++) {
			s[i].setStatus('over', false);
		}
	}
});
var UIComboEdit = new Class({
	Extends : UIComboBox,
	options : {
		'list' : {
			'multiselect' : false
		},
		'textInputParams' : {},
		'createParams' : {
			'class' : 'inline_block k_comboedit k_combo_btn'
		},
		'classPrefix' : 'k_comboedit'
	},
	setCaption : function(v) {
		if (!this.$notSetCaption)
			this.textInput.set('value', v);
	},
	find : function(v, a, up, down) {
		var s = this.list.getVisibleItems(), index = (up || down) ? 0 : -1;
		for ( var i = 0; i < s.length; i++) {
			if ((!a && s[i].getText().startsWith(v)) || (a && s[i].getText() == v)) {
				index = i;
				break;
			}
		}
		if (up) {
			if (index > 0)
				index--;
		} else if (down) {
			if (index < s.length - 1)
				index++;
		}
		if (index < 0)
			return {
				item : null
			};
		else
			return {
				item : s[index]
			};
	},
	clearChecked : function() {
		var ss = this.list.getSelItems();
		for ( var i = 0; i < ss.length; i++)
			ss[i].setStatus('check', false);
	},
	textInputKeyUp : function(e) {
		this.$notSetCaption = true;
		var finded = null, v = this.textInput.get('value');
		if (e.key == 'backspace' || e.key == 'delete') {
			finded = this.find(v, true);
		} else {
			if (UIInput.isCharKeyEvent(e)) {
				finded = this.find(v);
			}
		}
		if (finded) {
			this.clearChecked();
			if (finded.item != null) {
				var t = finded.item.getText();
				finded.item.setStatus('check', true);
				this.textInput.set('value', t);
				this.textInput.setSelectRange(v.length, t.length);
			}
		}
		delete this.$notSetCaption;
	},
	textInputKeyDown : function(e) {
		this.$notSetCaption = true;
		var finded = null, v = this.textInput.get('value');
		if (e.key == 'down') {
			finded = this.find(v, true, false, true);
			e.stop();
		} else if (e.key == 'up') {
			finded = this.find(v, true, true, false);
			e.stop();
		}
		if (finded) {
			this.clearChecked();
			if (finded.item != null) {
				var t = finded.item.getText();
				finded.item.setStatus('check', true);
				this.textInput.set('value', t);
				this.textInput.setSelectRange(v.length, t.length);
			}
		}
		delete this.$notSetCaption;
	},
	createContent : function(options) {
		var self = this;
		this.textInput = new Element('input', options['textInputParams']).inject(this.panel).addEvents({
			'keyup' : function(e) {
				self.textInputKeyUp.call(self, e);
			},
			'keydown' : function(e) {
				self.textInputKeyDown.call(self, e);
			}
		});
		this.createRightButton(options);
	},
	itemClick : function(item) {
		this.parent(item);
		this.textInput.focus();
	}
});
var UIChosenButton = new Class({
	Extends : UIComboButton,
	options : {
		'createParams' : {
			'class' : 'inline_block k_chosen_btn'
		},
		'classPrefix' : 'k_chosen_btn',
		'type' : 'check'
	},
	rightButtonClick : function() {
		this.fireEvent('closeClick');
	}
});
var UIChosenItem = new Class({
	Extends : UIListItem,
	options : {
		'type' : 'check'
	},
	isVisible : function() {
		return !this.status['check'] && !this.filtered;
	},
	setFiltered : function(v) {
		this.filtered = v;
		if (v)
			this.panel.setStyle('display', 'none');
		else
			this.panel.setStyle('display', '');
	}
});
var UIChosenBox = new Class({
	Extends : UIComboEdit,
	$oldfilter : '',
	options : {
		'list' : {
			'listItemParams' : {
				'createParams' : {
					'class' : 'k_chosenitem'
				},
				'classPrefix' : 'k_chosenitem'
			},
			'listItemClass' : UIChosenItem,
			'isRadio' : false
		},
		'chosenButtonParams' : {
			'labelParams' : {}
		},
		'createParams' : {
			'class' : 'inline_block k_chosenbox'
		},
		'classPrefix' : 'k_chosenbox'
	},
	selItems : [],
	createDropImg : function(options) {
	},
	setCaption : function(v) {
	},
	createRightButton : function() {
	},
	createContent : function(options) {
		var calcDiv = this.calcDiv = new Element('div').inject(new Element('div').setStyles({
			'position' : 'absolute',
			'width' : '0px',
			'overflow' : 'hidden'
		}).inject(this.panel)).setStyle('width', '100%');
		this.calcSpan = new Element('span', options['textInputParams']).inject(calcDiv);
		this.parent(options);
	},
	textInputKeyUp : function(e) {
		var v = this.textInput.get('value');
		this.calcSpan.set('text', v);
		var x = this.calcSpan.getSize().x, s = this.panel.getComputedSize();
		if (x > s.width)
			x = s.width;
		this.textInput.setStyle('width', this.calcSpan.getSize().x + 30);
		this.filter(v);
		if (this.window.isShowing()) {
			if (UIInput.isCharKeyEvent(e) || e.key == 'backspace')
				this.window.resetBounds(true);
		} else {
			this.dropdown();
			if (this.$selitem)
				this.$selitem.setStatus('over', true);
		}
	},
	textInputKeyDown : function(e) {
		if (e.key == 'down' || e.key == 'up') {
			var sel = this.$selitem, s = this.list.getVisibleItems(), index = s.indexOf(sel);
			if (s.length == 0)
				return;
			if (index < 0)
				index = 0;
			else
				index += (e.key == 'down' ? 1 : -1);
			if (index >= s.length)
				index = 0;
			else if (index < 0)
				index = s.length - 1;
			if (sel)
				sel.setStatus('over', false);
			sel = s[index];
			sel.setStatus('over', true);
			this.$selitem = sel;
		} else {
			if (e.key == 'enter' && this.$selitem) {
				this.itemClick(this.$selitem);
			}
			if (this.$selitem) {
				delete this.$selitem;
			}
		}
		if (e.key == 'backspace' && this.textInput.get('value').length == 0) {
			this.removeSelected();
			var s = this.selItems;
			if (s.length > 0) {
				var item = s[s.length - 1];
				if (!item.status['check'])
					item.setStatus('check', true);
			}
		} else
			this.clearSelected();
	},
	filter : function(str) {
		if (this.$oldfilter == str)
			return;
		delete this.$selitem;
		this.$oldfilter = str;
		var s = this.list.items;
		for ( var i = 0; i < s.length; i++) {
			s[i].setStatus('over', false);
			s[i].setFiltered(str != '' && !s[i].getText().contains(str));
		}
		this.list.visibleItemsChange();
	},
	doCreate : function(options) {
		this.parent(options);
		var s = this.list.getSelItems();
		for ( var i = 0; i < s.length; i++)
			this.createChosenButton(s[i]);
	},
	createChosenButton : function(item) {
		var ps = Object.clone(this.options['chosenButtonParams']), self = this;
		ps['panel'] = new Element('div').inject(this.textInput, 'before');
		ps['labelParams']['text'] = item.getText();
		var b = new UIChosenButton(ps).addEvents({
			'closeClick' : function() {
				self.closeClick.call(self, this);
			}
		});
		b.item = item;
		this.selItems.push(b);
	},
	clearSelected : function() {
		var s = this.selItems;
		for ( var i = 0; i < s.length; i++) {
			if (s[i].status['check'])
				s[i].setStatus('check', false);
		}
	},
	removeSelected : function() {
		var s = this.selItems, r = false;
		for ( var i = 0; i < s.length; i++) {
			if (s[i].status['check']) {
				this.closeClick(s[i]);
				r = true;
				i--;
			}
		}
		return r;
	},
	closeClick : function(item) {
		item.item.setStatus('check', false);
		item.panel.dispose();
		this.selItems.erase(item);
		this.collapse();
		this.list.visibleItemsChange();
	},
	itemClick : function(item) {
		this.createChosenButton(item);
		item.setStatus('check', true);
		item.setStatus('over', false);
		this.list.visibleItemsChange();
		this.collapse();
		this.textInput.set('value', '');
		this.textInput.focus();
	},
	valueChanged : function() {
	},
	click : function() {
		this.parent();
		this.textInput.focus();
	}
});
var UIUpDownInput = new Class({
	Extends : UIInput,
	options : {
		'updownParams' : {
			'class' : 'updown'
		},
		'createParams' : {
			'class' : 'inline_block k_input k_updown'
		},
		'inputParams' : {
			'value' : 0
		},
		'upParams' : {
			'createParams' : {
				'class' : 'up'
			},
			'labelParams' : {
				'class' : 'up_btn'
			},
			'classPrefix' : 'up',
			'mouseDownClicks' : true
		},
		'downParams' : {
			'createParams' : {
				'class' : 'down'
			},
			'labelParams' : {
				'class' : 'down_btn'
			},
			'classPrefix' : 'down',
			'mouseDownClicks' : true
		},
		'maxValue' : 100,
		'minValue' : 0
	},
	doCreate : function(options) {
		this.parent(options);
		this.createUpDown(options);
	},
	createUpDown : function(options) {
		var div = new Element('div', options['updownParams']).inject(this.panel).setSelectable(false), self = this;
		this.upButton = new UIButton(options['upParams']).inject(div).addEvents({
			'click' : function() {
				self.upDownClick.call(self, true);
			}
		});
		this.downButton = new UIButton(options['downParams']).inject(div).addEvents({
			'click' : function(s) {
				self.upDownClick.call(self, false);
			}
		});
	},
	upDownClick : function(isUp) {
		var min = this.options['minValue'], max = this.options['maxValue'];
		var v = this.textInput.get('value').toInt(min + 1);
		if (isUp && v > min)
			this.textInput.set('value', v - 1);
		else if (!isUp && v < max)
			this.textInput.set('value', v + 1);
		this.textInput.focus();
	},
	statusChanged : function(s) {
		this.parent(s);
		if (s == 'over') {
			this.upButton.setStatus('parent_over', this.status['over']);
			this.downButton.setStatus('parent_over', this.status['over']);
		}
	}
});
var UITimeEdit = new Class({
	Extends : UIUpDownInput,
	options : {
		'inputParams' : {
			value : '00:00:00'
		}
	},
	keyUp : function(e) {
		this.parent(e);
	},
	keyDown : function(e) {
		this.parent(e);
	},
	upDownClick : function(isUp) {
		// alert(this.input.getSelectStart());
		var min = this.options['minValue'], max = this.options['maxValue'];
		var v = this.textInput.get('value').toInt(min + 1);
		if (isUp && v > min)
			this.textInput.set('value', v - 1);
		else if (!isUp && v < max)
			this.textInput.set('value', v + 1);
		this.textInput.focus();
	}
});