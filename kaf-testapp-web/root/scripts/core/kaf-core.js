var Dimension = {
	makeRect : function(pos, size) {
		return {
			left : pos.x,
			top : pos.y,
			width : size.x,
			height : size.y,
			right : pos.x + size.x,
			bottom : pos.y + size.y
		};
	},
	makePos : function(x, y) {
		return {
			'x' : x,
			'y' : y
		};
	},
	offsetPos : function(pos, offset) {
		if (typeOf(offset) == 'number')
			offset = {
				'x' : offset,
				'y' : offset
			};
		return Dimension.makePos(pos.x + offset.x, pos.y + offset.y);
	},
	offsetRect : function(rect, offset) {
		if (typeOf(offset) == 'number')
			offset = {
				'x' : offset,
				'y' : offset
			};
		return Dimension.makeRect({
			'x' : rect.left + offset.x,
			'y' : pos.top + offset.y
		}, {
			'x' : rect.width,
			'y' : rect.height
		});
	},
	lockPosInRect : function(pos, rect) {
		pos = Object.clone(pos);
		if (pos.x < rect.left)
			pos.x = rect.left;
		if (pos.x > rect.right)
			pos.x = rect.right;
		if (pos.y < rect.top)
			pos.y = rect.top;
		if (pos.y > rect.bottom)
			pos.y = rect.bottom;
		return pos;
	},
	getRelativePos : function(pos, rect) {
		pos = Object.clone(pos);
		pos.x -= rect.left;
		pos.y -= rect.top;
		return pos;
	}
};
(function() {
	var arrayCombine = function(s, prefix, sp, from, dest) {
		if (prefix.length > 0)
			dest.push(prefix);
		for (var i = from; i < s.length; i++) {
			if (prefix.length > 0 && !prefix.endsWith(sp))
				prefix += sp;
			arrayCombine(s, prefix + s[i], sp, i + 1, dest);
		}
	};
	Object.repairContextUrl = function(o, n) {
		var url = o[n];
		if (url != undefined)
			o[n] = url.getContextUrl();
	};
	Number.isNumber = function(n) {
		if (n == null || n == undefined || n.toString().trim() == "")
			return false;
		else
			return !isNaN(n);
	};
	String.implement({
		getContextUrl : function() {
			if (this.startsWith('/'))
				return contextPath + this;
			else
				return this;
		},
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
		},
		decodeMasks : function() {
			var masks = [];
			var s = this.split('|'), oo = undefined;
			for (var i = 0; i < s.length; i++) {
				var s1 = s[i].split(','), o = {};
				var index = s1[0].indexOf('-');
				if (oo != null) {
					oo.next = o;
					o.prev = oo;
				}
				o['begin'] = s1[0].substr(0, index).toInt();
				o['end'] = s1[0].substring(index + 1).toInt();
				index = s1[1].indexOf('-');
				o['min'] = s1[1].substr(0, index).toInt();
				o['max'] = s1[1].substring(index + 1).toInt();
				oo = o;
				masks.push(o);
			}
			return masks;
		}
	});
	Array.implement({
		eraseAll : function(items) {
			for (var i = 0; i < items.length; i++)
				this.erase(items[i]);
			return this;
		},
		toString : function() {
			var r = '';
			for (var i = 0; i < this.length; i++) {
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
		},
		insert : function(o, from) {
			var i = this.indexOf(from);
			if (i >= 0)
				this.splice(i, 0, o);
			else
				this.push(o);
		}
	});
	Date.CH_WEEKS = [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ];
	Date.CH_MONTHS = [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" ];
	Date.WEEKS = [ "sun", "mon", "tue", "wed", "thu", "fri", "sat" ];
	Date.MONTH_DAYS = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];
	Date.implement({
		format : function(fmt) {
			var o = {
				"M+" : this.getMonth() + 1,
				"d+" : this.getDate(),
				"h+" : this.getHours() % 12 == 0 ? 12 : this.getHours() % 12,
				"H+" : this.getHours(),
				"m+" : this.getMinutes(),
				"s+" : this.getSeconds(),
				"q+" : Math.floor((this.getMonth() + 3) / 3),
				"S" : this.getMilliseconds()
			};
			var week = {
				"0" : "\u65e5",
				"1" : "\u4e00",
				"2" : "\u4e8c",
				"3" : "\u4e09",
				"4" : "\u56db",
				"5" : "\u4e94",
				"6" : "\u516d"
			};
			if (/(y+)/.test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
			}
			if (/(E+)/.test(fmt)) {
				fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "\u661f\u671f"
						: "\u5468") : "")
						+ week[this.getDay() + ""]);
			}
			for ( var k in o) {
				if (new RegExp("(" + k + ")").test(fmt)) {
					fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k])
							.substr(("" + o[k]).length)));
				}
			}
			return fmt;
		},
		getChineseDateString : function() {
			return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日";
		},
		getFullMonth : function() {
			return this.getFullYear() * 100 + (this.getMonth() + 1);
		},
		getFullDay : function() {
			return this.getFullYear() * 10000 + (this.getMonth() + 1) * 100 + this.getDate();
		},
		getDateString : function() {
			return this.getFullYear() + "-" + (this.getMonth() < 9 ? '0' : '') + (this.getMonth() + 1) + "-"
					+ (this.getDate() < 10 ? '0' : '') + this.getDate();
		},
		getDateTimeString : function() {
			return this.getDateString() + " " + this.getTimeString();
		},
		isSameDay : function(o) {
			return this.getFullYear() == o.getFullYear() && this.getMonth() == o.getMonth()
					&& this.getDate() == o.getDate();
		},
		getMonthString : function() {
			return this.getFullYear() + "-" + (this.getMonth() < 9 ? '0' : '') + (this.getMonth() + 1);
		},
		getTimeString : function() {
			return (this.getHours() < 10 ? "0" : "") + this.getHours() + ":" + (this.getMinutes() < 10 ? "0" : "")
					+ this.getMinutes() + ":" + (this.getSeconds() < 10 ? "0" : "") + this.getSeconds();
		},
		getMonthDays : function() {
			return Date.MONTH_DAYS[this.getMonth()];
		},
		getChineseWeekString : function() {
			return Date.CH_WEEKS[this.getDay()];
		},
		getWeekString : function() {
			return Date.WEEKS[this.getDay()];
		},
		milliSecondsBetween : function(dst) {
			return Math.abs(this.getTime() - dst.getTime());
		},
		secondsBetween : function(dst) {
			return this.milliSecondsBetween(dst) / 1000;
		},
		minutesBetween : function(dst) {
			return this.milliSecondsBetween(dst) / 60000;
		},
		hoursBetween : function(dst) {
			return this.milliSecondsBetween(dst) / 3600000;
		},
		daysBetween : function(dst) {
			return this.milliSecondsBetween(dst) / 86400000;
		},
		monthsBetween : function(dst) {
			return this.milliSecondsBetween(dst) / (86400000 * 30.4375);
		},
		yearsBetween : function(dst) {
			return this.milliSecondsBetween(dst) / (86400000 * 365.25);
		},
		setYear : function(y) {
			this.setFullYear(y);
		},
		add : function(field, amount) {
			switch (field) {
			case 'd':
				return new Date(this.getTime() + amount * 86400000);
			case 'y': {
				var r = new Date(this.getTime());
				r.setFullYear(this.getFullYear() + amount);
				return r;
			}
			case 'm': {
				var r = new Date(this.getTime());
				var y = Math.floor(amount / 12);
				amount -= y * 12;
				var m = r.getMonth() + amount;
				if (m < 0) {
					y--;
					m = 12 + m;
				} else if (m > 11) {
					y++;
					m -= 12;
				}
				r.setFullYear(this.getFullYear() + y);
				r.setMonth(m);
				return r;
			}
			case 'h':
				return new Date(this.getTime() + amount * 3600000);
			case 'n':
				return new Date(this.getTime() + amount * 60000);
			case 's':
				return new Date(this.getTime() + 1000 * amount);
			case 'mi':
				return new Date(this.getTime() + amount);
			}
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
			var bs = this.getBorders();
			if (bounds['width'] != undefined)
				this.setStyle('width', (bounds['width'] - bs['hor']) + 'px');
			if (bounds['height'] != undefined)
				this.setStyle('height', (bounds['height'] - bs['ver']) + 'px');
			if (bounds['left'] != undefined)
				this.setStyle('left', bounds['left'] + 'px');
			if (bounds['top'] != undefined)
				this.setStyle('top', bounds['top'] + 'px');
		},
		getBounds : function() {
			return Dimension.makeRect(this.getPosition(), this.getSize());
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
		getInputElements : function(type) {
			var e = this.getElements('input');
			if (type != undefined) {
				var a = [];
				for (var i = 0; i < e.length; i++) {
					var t = e[i].type || 'text';
					if (t == type)
						a.push(e[i]);
				}
				return a;
			} else
				return e;
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
				for (var i = 0; i < a.length; i++)
					this.addClass(a[i]);
			} else
				this.addClass(a);
			if (typeOf(r) == 'array') {
				for (var i = 0; i < r.length; i++)
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
			return Dimension.makeRect(this.getScroll(), this.getSize());
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
		},
		checkInlineBlock : function(p) {
			if (Browser.ie && Browser.version < 8) {
				var dispose = false;
				if (this.getParent() == null && p) {
					this.inject(p);
					dispose = true;
				}
				if (this.getStyle('display') == 'inline-block') {
					this.setStyles({
						'display' : 'inline',
						'zoom' : 1
					});
				}
				if (dispose)
					this.dispose();
			}
		},
		setLoadingPrompt : function(p) {
			this.set('html', '<div class="el_loading">' + (p ? p : '载入中，请稍候...') + '</div>');
			return this;
		},
		setLoadErrorPrompt : function(p) {
			this.set('html', '<div class="el_loaderror">' + (p ? p : '载入失败，请稍候再次尝试.') + '</div>');
			return this;
		},
		setMinHeight : function(h) {
			if (typeOf(h) != 'number')
				h = $(h).getComputedSize().totalHeight;
			if (Browser.ie6)
				this.setStyle('_height', h + 'px');
			else
				this.setStyle('min-height', h + 'px');
		},
		dynamicLoad : function(ps) {
			var _this = this;
			var ejed = false, delaytimer = null;
			var ad = function() {
				if (ps["minheight"]) {
					_this.setMinHeight(ps['minheight']);
				}
			};
			var ej = function() {
				if (delaytimer)
					clearTimeout(delaytimer);
				if (ejed)
					return;
				ejed = true;
				var pf = ps['events']['prepareUpdate'];
				if (typeOf(pf) == 'function')
					pf();
			};
			var ed = function() {
				var cf = ps['events']['complete'];
				if (typeOf(cf) == 'function')
					cf();
			};
			if (ps.url) {
				var _lf = ps['events']['loading'];
				var _ef = ps['events']['error'];
				var ld = ps['events']['load'];
				var lf = function() {
					if (typeOf(_lf) == 'function')
						_lf();
					else {
						delaytimer = (function() {
							_this.setLoadingPrompt();
							ej();
							ad();
						}).delay(500);
					}
				};
				var ef = function(c) {
					if (typeOf(_ef) == 'function')
						_ef();
					else
						_this.setLoadErrorPrompt();
					ej();
					ed();
					ad();
				};
				lf();
				var ff = function() {
					var url = ps['url'];
					if (url.startsWith("/"))
						url = contextPath + url;
					if (typeOf(ps['params']) == 'string') {
						if (url.indexOf('?') > 0)
							url += '&' + p['params'];
						else
							url += '?' + p['params'];
					}
					new Request.HTML({
						data : _this,
						link : "cancel",
						update : _this,
						noCache : true,
						method : "get",
						onUpdateElement : function() {
							ej();
						},
						onFailure : function() {
							ef(1);
						},
						onTimeout : function() {
							ef(2);
						},
						onException : function() {
							ef(3);
						},
						onSuccess : function() {
							ed();
							if (typeOf(ld) == 'function')
								ld();
							ad();
						},
						'url' : url
					}).send();
				};
				if (ps.jsCssFiles) {
					ps['jsCssFileMgr'].load({
						'files' : ps.jsCssFiles,
						'events' : {
							'load' : function() {
								ff.delay(10);
							},
							'error' : ef
						}
					});
				} else
					ff.delay(10);
			}
		}
	});
})();
var UIClass = new Class({
	Implements : [ Events, Options ],
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		if (this.options['events']) {
			this.addEvents(this.options['events']);
			delete this.options['events'];
		}
	}
});
var HttpRequest = new Class({
	Extends : UIClass,
	request : null,
	initialize : function(options) {
		this.parent(options);
		var self = this;
		Object.repairContextUrl(this.options, 'url');
		this.request = new Request(Object.merge(this.options, {
			onFailure : function() {
				self.error(1, '网络故障，请稍候再试...');
			},
			onTimeout : function() {
				this.cancel();
				self.error(2, '网络超时，请稍候再试...');
			},
			onException : function() {
				self.error(3, '网络异常，请稍候再试...');
			},
			onSuccess : function(text, xml) {
				self.success(text, xml);
			}
		}));
	},
	error : function(code, msg, o) {
		this.fireEvent('error', Object.merge({
			'errCode' : code,
			'errMsg' : msg
		}, o));
	},
	success : function(text, xml, o) {
		var q = this.$queue;
		if (q != undefined && q.length > 0) {
			var data = q[0];
			q.erase(data);
			if (typeOf(data) == 'object') {
				Object.repairContextUrl(data, 'url');
			}
			this.request.send(data);
		} else
			this.fireEvent('success', Object.merge({
				'text' : text,
				'xml' : xml
			}, o));
	},
	send : function(data) {
		if (typeOf(data) == 'array') {
			var q = this.$queue = data;
			if (q.length == 0)
				return;
			data = q[0];
			q.erase(data);
		} else {
			delete this.$queue;
		}
		if (typeOf(data) == 'object' && data.url) {
			Object.repairContextUrl(data, 'url');
			this.request.options['url'] = data['url'];
			delete data.url;
		}
		this.request.send(data);
		return this;
	},
	cancel : function() {
		this.request.cancel();
		return this;
	}
});
var XmlRequest = new Class({
	Extends : HttpRequest,
	success : function(text, xml) {
		var c = xml.getElementsByTagName('result').item[0];
		if (c.getAttribute("success") == 'true')
			this.parent(text, xml, {
				'data' : xml
			});
		else
			this.error(0, c.getAttribute('message'), {
				'data' : xml
			});
	}
});
var JsonRequest = new Class({
	Extends : HttpRequest,
	success : function(text, xml) {
		var r = JSON.decode(text), t = this.options['textfield'];
		if (t != undefined && r.items != undefined) {
			for (var i = 0; i < r.items.length; i++) {
				r.items[i]['text'] = r.items[i][t];
			}
		}
		if (!r['result']['success'])
			this.error(0, r['result']['message'], {
				'data' : r
			});
		else
			this.parent(text, xml, {
				'data' : r
			});
	}
});
var JsCssFileManager = new Class({
	initialize : function() {
		this.files = [];
	},
	load : function(ps) {
		var a = typeOf(ps['files']) == 'array' ? ps['files'] : [ ps['files'] ];
		var _this = this;
		var fs = _this.files;
		var e = function(o) {
			var url = o['url'].startsWith('/') ? contextPath + o['url'] : o['url'];
			for (var i = 0; i < fs.length; i++) {
				var o1 = fs[i];
				if (o['type'] == o1['d']['type'] && o['url'] == o1['d']['url']) {
					if (o1.disposed) {
						o1['el'].inject($$('head')[0]);
						delete o1.disposed;
						o1.addref = 1;
					} else
						o1.addref++;
					return true;
				}
			}
			var s = o['type'] == 'css' ? $$('link') : $$('script');
			for (var i = 0; i < s.length; i++) {
				if (o['type'] == 'css') {
					if (url == s[i].href)
						return true;
				} else if (url == s[i].src)
					return true;
			}
			return false;
		};
		var c = function(o, t) {
			var obj;
			var d = document;
			if (o['type'] == 'css') {
				obj = d.createElement('style');
				obj.setAttribute('rel', 'stylesheet');
				obj.setAttribute('type', 'text/css');
				if (obj.styleSheet)
					obj.styleSheet.cssText = t;
				else
					obj.innerText = t;
			} else {
				obj = d.createElement('script');
				obj.setAttribute('type', 'text/javascript');
				obj.text = t;
			}
			if (obj) {
				$$('head')[0].appendChild(obj);
				o.addref = 1;
				fs.push({
					'd' : o,
					'el' : $(obj)
				});
			}
		};
		var index = 0;
		var f = function(o) {
			new HttpRequest({
				url : o['url'],
				'method' : 'get',
				'events' : {
					'success' : function(e) {
						c(o, e['text']);
						n();
					},
					'error' : function(e) {
						var ff = ps['events'] ? ps['events']['error'] : undefined;
						if (typeOf(ff) == 'function')
							ff(e.errMsg + "[" + e.errCode + "]");
					}
				}
			}).send();
			return true;
		};
		var n = function() {
			while (index < a.length) {
				var o = a[index++];
				if (!e(o)) {
					f(o);
					return true;
				}
			}
			var ff = ps['events'] ? ps['events']['load'] : undefined;
			if (typeOf(ff) == 'function')
				ff();
			return false;
		};
		n();
	},
	clean : function(files) {
		var a = typeOf(files) == 'array' ? files : [ files ];
		for (var i = 0; i < a.length; i++) {
			var o = a[i];
			for (var j = 0; j < this.files.length; j++) {
				var dd = this.files[j];
				if (dd.addref > 1)
					dd.addref--;
				else if (o.type == dd.d.type && o.url == dd.d.url) {
					dd['el'].dispose();
					dd.disposed = true;
					break;
				}
			}
		}
	},
	cleanJs : function() {
		for (var i = 0; i < this.files.length; i++) {
			var o = this.files[i];
			if (o.addref > 1)
				o.addref--;
			else if (o['el'].get('type') == 'text/javascript') {
				o.disposed = true;
				o['el'].dispose();
				i--;
			}
		}
	},
	cleanCss : function() {
		for (var i = 0; i < this.files.length; i++) {
			var o = this.files[i];
			if (o.addref > 1)
				o.addref--;
			else if (o['el'].get('type') == 'text/css') {
				this.files.erase(o);
				o['el'].dispose();
				o.disposed = true;
			}
		}
	},
	cleanAll : function() {
		for (var i = 0; i < this.files.length; i++) {
			var o = this.files[i];
			if (o.addref > 1)
				o.addref--;
			else {
				o['el'].dispose();
				o.disposed = true;
			}
		}
	}
});
var UIControl = new Class({
	Extends : UIClass,
	status : {
		'over' : false
	},
	availableStatus : [ 'over' ],
	options : {
		'createParams' : {}
	},
	initialize : function(options) {
		this.parent(options);
		this.create();
		this.$created = true;
		this.updateUI();
	},
	addClass : function(c) {
		this.panel.addClass(c);
		return this;
	},
	removeClass : function(c) {
		this.panel.removeClass(c);
		return this;
	},
	addRemoveClass : function(a, r) {
		this.panel.addRemoveClass(a, r);
		return this;
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
	dispose : function() {
		this.panel.dispose();
	},
	destroy : function() {
		this.panel.destroy();
		delete this.panel;
	},
	getValueInput : function() {
		if (!this.valueInput) {
			var id = this.options['input'];
			var input = $(id);
			if (!input && typeOf(id) == 'string') {
				input = new Element('input', {
					'type' : 'hidden',
					'id' : id,
					'name' : id
				}).inject(this.panel);
			}
			this.valueInput = input;
		}
		return this.valueInput;
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
	updateUI : function() {
	},
	statusChanged : function(s) {
		this.updateUI();
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
				var s = Object.clone(e['bounds']), panel = this.panel, borders = panel.getBorders();
				e['bounds'].height -= borders.ver;
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
			pos.x -= bodyView.left;
			pos.y -= bodyView.top;
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
			'width' : w,
			'height' : h
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
		if (options['mask'] != undefined)
			this.mask = new Element('div', options['mask']);
	},
	bringToFront : function() {
		this.parent();
		if (this.mask)
			this.mask.inject(this.panel, 'before');
	},
	resetBounds : function() {
		this.parent();
		if (this.mask)
			this.mask.setBounds($(document.body).getViewBounds());
	},
	doHide : function() {
		this.parent();
		if (this.mask)
			this.mask.dispose();
	}
});
var UIInput = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'inline_block k_input_common k_input'
		},
		'inputParams' : {
			'type' : 'text'
		},
		'containerParams' : {
			'class' : 'container'
		},
		'placeholderParams' : {
			'labelClass' : 'placeholder'
		}
	},
	doCreate : function(options) {
		var self = this;
		if (options['masks'] != undefined)
			this.masks = options['masks'].decodeMasks();
		this.parent(options);
		if (options['containerParams'])
			this.container = new Element('div', options['containerParams']).inject(this.panel);
		else
			this.container = this.panel;
		this.panel.addEvents({
			'click' : function(e) {
				self.click(e);
			}
		});
		this.createInput(options['inputParams']);
		if (options['windowParams'] != undefined) {
			this.createWindow(options);
			this.addWindowEvents();
		}
		this.panel.checkInlineBlock();
	},
	createInput : function(options) {
		var self = this;
		this.textInput = new Element('input', options).inject(this.container).addEvents({
			'click' : function(e) {
				self.click(e);
			},
			'keydown' : function(e) {
				self.keyDown.call(self, e);
			},
			'keyup' : function(e) {
				self.keyUp.call(self, e);
			}
		});
		if (options['value'])
			this.setValue(options['value']);
		if (options['placeholder'])
			this.doPlaceHolder(options['placeholder']);
	},
	click : function(e) {
		if (e) {
			if (e.target == this.panel) {
				var input = this.textInput;
				if (input) {
					var r = this.panel.getComputedSize(), r1 = input.getComputedSize();
					if (r1.totalWidth != r.width)
						input.setStyle('width', r.width - r1.computedLeft - r1.computedRight);
					input.focus();
				}
			}
		}
	},
	getText : function() {
		return this.textInput.get('value');
	},
	setText : function(v) {
		this.textInput.set('value', v);
	},
	setValue : function(v) {
		if (this.valueInput)
			this.valueInput.set('value', v);
		else
			this.setText(v);
	},
	getValue : function() {
		if (this.valueInput)
			return this.valueInput.get('value');
		else
			return this.getText();
	},
	doPlaceHolder : function(placeHolder) {
		if (Element.placeHolderSupported)
			return;
		var input = this.textInput;
		this.placeHolder = new Element('label', {
			'class' : this.options['placeholderParams']['labelClass'],
			'text' : placeHolder,
			'events' : {
				'mousedown' : function(e) {
					e.stop();
					document.documentElement.fireEvent('mousedown', e);
					input.focus();
				}
			}
		}).inject(this.valueInput, 'before');
	},
	keyUp : function(e) {
		var p = this.placeHolder;
		if (p && UIInput.isCharKeyEvent(e) && input.get('value') == '')
			p.setStyle('display', 'none');
	},
	validate : function(v) {
		return v;
	},
	getCaretMask : function() {
		var selstart = this.textInput.getSelectStart(), m = this.masks;
		if (m) {
			for (var i = 0; i < m.length; i++) {
				if (selstart >= m[i].begin && selstart <= m[i].end)
					return m[i];
				else if (i < m.length - 1) {
					if (selstart > m[i].end && selstart < m[i + 1].end)
						return m[i + 1];
				}
			}
		}
		return null;
	},
	keyDown : function(e) {
		if (e.key == 'tab' && this.window)
			this.collapse();
		var p = this.placeHolder, input = this.textInput;
		if (p) {
			if (input.get('value') != '') {
				p.setStyle('display', 'none');
			} else
				p.setStyle('display', '');
		}
		if (!this.masks)
			return;
		var m = this.getCaretMask();
		if (!m)
			return;
		var selstart = input.getSelectStart(), v = input.get('value'), ov = v;
		if (!(e.key == 'left' || e.key == 'right' || e.key == 'tab'))
			e.stop();
		if (e.code == 8) {
			if (selstart > 0) {
				if (selstart > m.begin) {
					v = this.validate(v.substr(0, selstart - 1) + '0' + v.substring(selstart));
					var num = v.substr(m.begin, m.end).toInt(0);
					if ((m.min == undefined || num >= m.min) && (m.max == undefined || num <= m.max)) {
						input.set('value', v);
					}
				}
				selstart--;
				if (selstart == m.begin) {
					if (m.prev)
						selstart = m.prev.end;
				}
			}
		} else if (e.code >= 48 && e.code <= 57) {
			if (selstart >= m.begin && selstart < m.end) {
				v = this.validate(v.substr(0, selstart) + e.key + v.substring(selstart + 1));
				var num = v.substr(m.begin, m.end).toInt(0);
				if ((m.min == undefined || num >= m.min) && (m.max == undefined || num <= m.max)) {
					input.set('value', v);
				}
				selstart++;
				if (selstart == m.end) {
					if (m.next)
						selstart = m.next.begin;
				}
				input.setSelectRange(selstart, selstart);
			}
		} else if (e.key == 'up') {
			this.upDownClick(true);
			return;
		} else if (e.key == 'down') {
			this.upDownClick(false);
			return;
		}
		if (ov != this.textInput.get('value'))
			this.change();
		input.setSelectRange(selstart, selstart);
	},
	upDownClick : function(isUp) {
		var input = this.textInput, v = input.get('value'), ov = v, s = 0, e = 0, min = undefined, max = undefined;
		var m = this.getCaretMask();
		if (m) {
			s = m.begin;
			e = m.end;
			min = m.min, max = m.max;
		}
		var num = e == 0 ? v.toInt(0) : v.substring(s, e).toInt(0);
		if (!isUp) {
			if (min == undefined || num > min)
				num--;
		} else {
			if (max == undefined || num < max)
				num++;
		}
		num += '';
		if (e > 0) {
			for (; num.length < e - s;)
				num = '0' + num;
			v = v.substr(0, s) + num + v.substring(e);
		} else {
			v = num;
			e = v.length;
		}
		input.set('value', v);
		if (ov != this.textInput.get('value'))
			this.change();
		input.focus();
		input.setSelectRange(s, e);
	},
	change : function() {
		this.fireEvent('change');
	},
	windowShow : function() {
	},
	windowHide : function() {
	},
	dropdown : function() {
		this.window.show();
	},
	collapse : function() {
		this.window.hide();
	}
});
UIInput.isCharKeyEvent = function(e) {
	var c = e.code;
	return (c >= 65 && c <= 90) || (c >= 48 && c <= 57) || (c >= 186 && c <= 192) || (c >= 219 && c <= 222) || c == 32
			|| (c >= 96 && c <= 111);
};
UIButton = new Class({
	Extends : UIControl,
	availableStatus : [ 'down', 'over', 'over_down', 'disabled' ],
	options : {
		'createParams' : {
			'class' : 'inline_block k_comm_button k_button'
		},
		'containerParams' : {
			'class' : 'container'
		},
		'group' : 0,
		'status' : {
			'down' : false,
			'over' : false,
			'disabled' : false
		},
		'type' : 'button',
		'labelParams' : {
			'class' : 'buttonlabel'
		},
		'classPrefix' : 'k_comm_button',
		'mouseDownClicks' : false
	},
	createIcon : function(options) {
		if (options['iconParams'])
			this.icon = new Element('img', options['iconParams']).inject(this.container).setSelectable(false);
	},
	createContent : function(options) {
		this.captionLabel = new Element('div', options['labelParams']).inject(this.container).setSelectable(false);
	},
	doCreate : function(options) {
		this.status = Object.clone(options['status']);
		if (options['type'] == 'radio' && this.gorup != 0) {
			if (!UIButton.GroupButtons[options['group']])
				UIButton.GroupButtons[options['group']] = [];
			UIButton.GroupButtons[options['group']].push(this);
		}
		this.parent(options);
		if (options['containerParams'])
			this.container = new Element('div', options['containerParams']).inject(this.panel);
		else
			this.container = this.panel;
		this.createIcon(options);
		this.createContent(options);
		this.panel.checkInlineBlock();
		var self = this;
		this.panel.addEvents({
			'mouseover' : function() {
				self.setStatus('over', true);
			},
			'mouseout' : function() {
				self.setStatus('over', false);
			},
			'mousedown' : function(e) {
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
				if (e.rightClick)
					return;
				clearInterval(self.$mtimer);
				delete self.$mtimer;
				self.panel.stopCapture();
				self.setStatus('down', false);
			},
			'click' : function(e) {
				if (!self.$mousedownclicked) {
					self.click(e);
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
		if (s['disabled'])
			return 'disabled';
		else if (s['down'] || s['check']) {
			if (s['over'] || s['parent_over'])
				return 'over_down';
			else
				return 'down';
		} else
			return this.parent();
	},
	updateUI : function() {
		var d = this.options['classPrefix'];
		var a = this.getComputedStatus(), r = [], as = this.availableStatus;
		for (var i = 0; i < as.length; i++) {
			if (a != as[i])
				r.push(d + '_' + as[i]);
		}
		this.panel.addRemoveClass(a.length > 0 ? d + '_' + a : '', r);
	},
	statusChanged : function(s) {
		this.parent(s);
		if (this.options['group'] != 0 && s == 'check' && this.getStatus('check') && this.options['type'] == 'radio') {
			var ss = UIButton.GroupButtons[this.options['group']];
			for (var i = 0; i < ss.length; i++) {
				if (ss[i] != this && ss[i].getStatus('check'))
					ss[i].setStatus('check', false);
			}
		}
	},
	click : function(e) {
		if (this.getStatus('disabled'))
			return;
		switch (this.options['type']) {
		case 'radio':
			this.setStatus('check', true);
			break;
		case 'check':
			this.setStatus('check', !this.status['check']);
			break;
		}
		this.fireEvent('click', e);
	}
});
UIButton.GroupButtons = {};
var UIComboInput = new Class({
	Extends : UIInput,
	options : {
		'createParams' : {
			'class' : 'inline_block k_input k_combo'
		},
		'buttonParams' : {
			'containerParams' : undefined,
			'createParams' : {
				'class' : 'drop_btn'
			},
			'labelParams' : {
				'class' : 'drop_btn_l'
			},
			'classPrefix' : 'drop_btn'
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.createButton(options);
	},
	createButton : function(options) {
		var self = this;
		this.button = new UIButton(options['buttonParams']).inject(this.container).addEvent('click', function(e) {
			self.buttonClick(e);
		});
	},
	click : function(e) {
		if (this.textInput.get('readonly')) {
			this.buttonClick(e);
		} else
			this.parent(e);
	},
	buttonClick : function(e) {
		if (e)
			e.stop();
		if (this.window) {
			if (this.window.isShowing())
				this.window.hide();
			else
				this.window.show();
		}
		this.fireEvent('buttonClick');
	},
	keyUp : function(e) {
		this.parent(e);
		if (!this.textInput.get('readonly') && e.key == 'enter')
			this.buttonClick();
	},
	statusChanged : function(s) {
		if (s == 'over' && this.button)
			this.button.setStatus('parent_over', this.status['over']);
		this.parent(s);
	}
});
var UIListItem = new Class({
	Extends : UIButton,
	options : {
		'type' : 'radio',
		'createParams' : {
			'class' : 'k_listitem'
		},
		'classPrefix' : 'k_listitem',
		'labelParams' : {
			'class' : 'lilabel'
		}
	},
	createContent : function(options) {
		var d = options['data'];
		options['labelParams']['text'] = d['text'];
		this.parent(options);
		if (this.icon) {
			if (d.img)
				this.icon.src = d.img.getContextUrl();
		}

	},
	getContentPanel : function() {
		return this.captionLabel;
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
	},
	statusChanged : function(s) {
		this.parent(s);
		if (s == 'check') {
			if (this.options['type'] == 'radio' && this.status['check'] && this.list) {
				var items = this.list.items;
				for (var i = 0; i < items.length; i++) {
					if (items[i] != this)
						items[i].setStatus('check', false);
				}
			}
			if (this.list)
				this.list.updateInput();
		}
	}
});
var UIPageSelector = new Class({
	Extends : UIControl,
	'options' : {
		'createParams' : {
			'class' : 'pageselector'
		},
		'visiblepages' : 6
	},
	setPages : function(pages, c) {
		this.panel.empty();
		this.panel.setSelectable(false);
		if (pages <= 0) {
			this.panel.setStyle('display', 'none');
			return;
		}
		this.pages = pages;
		this.panel.setStyle('display', 'block');
		var ps = this.options;
		var _this = this;
		var createButton = function(text, disabled, current, page) {
			var o = new Element("span", {
				'class' : (disabled ? 'disabled' : (current ? 'current' : 'normal')),
				'text' : text
			}).inject(_this.panel).setSelectable(false);
			o.disabled = disabled;
			o.current = current;
			o.page = page;
			o.addEvent('mouseover', function() {
				if (!o.disabled && !o.current)
					o.className = 'over';
			});
			o.addEvent('mouseout', function() {
				if (!o.disabled && !o.current)
					o.className = 'normal';
			});
			o.addEvent('click', function() {
				if (!o.disabled && !o.current) {
					if (o.page == -1) {
						if (_this.page > 1)
							_this.goPage(_this.page - 1);
					} else if (o.page == -2) {
						if (_this.page < pages)
							_this.goPage(_this.page + 1);
					} else
						_this.goPage(o.page);
				}
			});
			return o;
		};
		this.page = 1;
		this.prevButton = createButton('< Prev', true, false, -1);
		this.currentButton = this.firstButton = createButton('1', false, true, 1);
		this.buttons = [];
		var vp = ps['visiblepages'];
		if (vp == undefined)
			vp = 6;
		if (vp > pages)
			vp = pages;
		if (pages > vp)
			this.prevEllipse = new Element('span', {
				'class' : 'ellipse',
				text : ' ... ',
				'styles' : {
					'display' : 'none'
				}
			}).inject(this.panel);
		for (var i = 0; i < vp - 2; i++) {
			this.buttons[i] = createButton(i + 2, false, false, i + 2);
		}
		if (pages > vp)
			this.nextEllipse = new Element('span', {
				'class' : 'ellipse',
				text : ' ... '
			}).inject(this.panel);
		if (pages > 1) {
			this.lastButton = createButton(pages, false, false, pages);
		}
		this.nextButton = createButton('Next >', 1 == pages, false, -2);
		if (c != undefined) {
			this.pageSpan = new Element('span', {
				'class' : 'prompt',
				'text' : '共 ' + c + ' 条记录'
			}).inject(this.panel);
			this.count = c;
		}
	},
	goPage : function(page, nochange) {
		if (this.options == undefined || this.pages == undefined)
			return;
		var pages = this.pages;
		if (page < 1)
			page = 1;
		if (page > pages)
			page = pages;
		if (page == this.page)
			return;
		this.currentButton.current = false;
		this.currentButton.className = 'normal';
		var vp = this.options['visiblepages'];
		if (vp == undefined)
			vp = 6;
		if (vp > pages)
			vp = pages;
		var num = Math.ceil(vp / 2);
		var o = null;
		if (pages > vp) {
			var start;
			var v1 = true, v2 = true;
			if (page <= num) {
				v1 = false;
				start = 2;
			} else if (page >= pages - num) {
				start = pages - num - 1;
				v2 = false;
			} else {
				start = Math.ceil(page - (vp - 2) / 2);
			}
			for (var i = start; i < start + vp - 2; i++) {
				var b = this.buttons[i - start];
				b.page = i;
				b.set('text', i);
				if (page == i) {
					o = b;
				}
			}
			this.prevEllipse.setStyle('display', v1 ? '' : 'none');
			this.nextEllipse.setStyle('display', v2 ? '' : 'none');
		}
		if (page == 1)
			o = this.firstButton;
		else if (page == pages)
			o = this.lastButton;
		else if (o == null)
			o = this.buttons[page - 2];
		o.current = true;
		o.className = 'current';
		this.page = page;
		this.currentButton = o;
		if (page == 1) {
			if (this.prevButton) {
				this.prevButton.disabled = true;
				this.prevButton.className = 'disabled';
			}
		} else {
			if (this.prevButton) {
				this.prevButton.disabled = false;
				this.prevButton.className = 'normal';
			}
		}
		if (page == pages) {
			if (this.nextButton) {
				this.nextButton.disabled = true;
				this.nextButton.className = 'disabled';
			}
		} else {
			if (this.nextButton) {
				this.nextButton.disabled = false;
				this.nextButton.className = 'normal';
			}
		}
		if (!nochange)
			this.fireEvent('change');
	}
});
var UIListControl = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'inline_block border k_list'
		},
		'listPanelParams' : {},
		'listItemParams' : {
			'events' : {}
		},
		'emptyParams' : {
			'class' : 'empty_item',
			'text' : '无可选项'
		},
		'loadingParams' : {
			'class' : 'loading',
			'error-class' : 'loading_error'
		},
		'multiselect' : false,
		'listItemClass' : UIListItem,
		'requestParams' : {},
		'requestData' : {
			'maxresults' : 12,
			'firstindex' : 0
		},
		'initload' : true
	},
	request : null,
	items : [],
	isFirstLoad : true,
	createSearchInput : function(options) {
		var s = options['searchInputParams'], self = this;
		if (s != undefined) {
			this.searchInput = new UIComboInput(Object.merge(s, {
				'parent' : this.panel,
				'events' : {
					'buttonClick' : function(e) {
						self.load();
					}
				},
				'createParams' : {
					'class' : 'k_list_search_combo k_input'
				},
				'buttonParams' : {
					'labelParams' : {
						'class' : 'search_btn_l'
					}
				}
			}));
		}
	},
	createNextButton : function(options) {
		var s = options['nextButtonParams'], self = this;
		if (s != undefined) {
			var ss = Object.clone(s);
			if (ss['buttonParams'])
				delete ss['buttonParams'];
			this.nextButton = new UIButton(Object.merge(s['buttonParams'] || {}, {
				'parent' : this.nextButtonPanel = new Element('div', Object.merge(ss, {
					'class' : 'k_list_next_p'
				})).inject(this.panel),
				'events' : {
					'click' : function(e) {
						self.load(true);
					}
				},
				'labelParams' : {
					'text' : '加载下一页'
				}
			}));
		}
	},
	createEmptyItem : function(options) {
		if (options['emptyParams']['text'] != undefined) {
			this.emptyItem = new Element('div', options['emptyParams']).inject(this.listPanel).setStyle('display',
					'none');
		}
	},
	createLoadingItem : function(options) {
		if (options['requestParams']['url'] != undefined) {
			this.loadingItem = new Element('div', options['loadingParams']);
		}
	},
	createRequest : function(options) {
		var ps = options['requestParams'];
		if (ps == undefined)
			return;
		var self = this;
		this.request = new JsonRequest(Object.merge(ps, {
			'events' : {
				'success' : function(e) {
					clearTimeout(self.$loadingTimerId);
					self.loadSuccess(e);
				},
				'error' : function(e) {
					clearTimeout(self.$loadingTimerId);
					self.loadError(e);
				}
			}
		}));
	},
	loadError : function(e) {
		if (this.loadingItem) {
			this.loadingItem.inject(this.getLoadingItemParent());
			this.loadingItem.className = this.options['loadingParams']['error-class'];
			this.loadingItem.set('text', '载入失败.');
		}
		this.visibleItemsChange();
	},
	loadSuccess : function(e) {
		if (this.needClearItems || (e.data.count != undefined && e.data.count > 0))
			this.recordCount = e.data.count;
		if (this.loadingItem)
			this.loadingItem.dispose();
		if (this.isFirstLoad) {
			this.isFirstLoad = false;
		}
		if (this.needClearItems)
			this.clearItems();
		this.addItems(e.data.items, this.loadParent);
		this.fireEvent('loadSuccess', e);
	},
	getLoadingItemParent : function() {
		return this.panel;
	},
	loadStart : function() {
		if (this.loadingItem) {
			this.loadingItem.inject(this.getLoadingItemParent());
			this.loadingItem.className = this.options['loadingParams']['class'];
			this.loadingItem.set('text', '载入中...');
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.panel.checkInlineBlock();
		this.getValueInput();
		this.createSearchInput(options);
		this.listPanel = new Element('div', options['listPanelParams']).inject(this.panel);
		this.createLoadingItem(options);
		this.createRequest(options);
		this.createEmptyItem(options);
		this.createNextButton(options);
		if (options['value'] != undefined)
			this.valueInput.set('value', options['value']);
		if (options['items'] != undefined) {
			this.resetItems(options['items']);
			this.visibleItemsChange();
		} else if (this.request && options.initload)
			this.load();
	},
	clearItems : function() {
		for (var i = 0; i < this.items.length; i++) {
			this.items[i].destroy();
		}
		this.items = [];
	},
	load : function(next, parent) {
		var self = this;
		if (this.nextButtonPanel)
			this.nextButtonPanel.dispose();
		var sb = this.searchInput, data = this.options['requestData'];
		switch (typeOf(next)) {
		case 'object':
			data = Object.merge(data, next);
			this.options['requestData'] = data;
			next = false;
			break;
		default:
			data['firstindex'] = !next ? -1 : this.items.length;
			if (sb != undefined)
				data['keyword'] = sb.getText();
		}
		this.needClearItems = !next && !parent;
		this.loadSendData = data;
		this.loadParent = parent;
		this.request.cancel().send({
			'data' : data
		});
		clearTimeout(this.$loadingTimerId);
		this.$loadingTimerId = function() {
			self.loadStart();
		}.delay(1000);
	},
	findByText : function(o) {
		for (var i = 0; i < this.items.length; i++) {
			var d = this.items[i]['options']['data'], v = d['text'];
			if (v == o)
				return this.items[i];
		}
		return null;
	},
	find : function(o) {
		for (var i = 0; i < this.items.length; i++) {
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
			if (this.valueInput)
				this.valueInput.set('value', v);
			this.fireEvent('valueChanged');
		}
	},
	getSelItems : function() {
		var s = [];
		for (var i = 0; i < this.items.length; i++) {
			if (this.items[i].status['check']) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getUnselItems : function() {
		var s = [];
		for (var i = 0; i < this.items.length; i++) {
			if (!this.items[i].status['check']) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getVisibleItems : function() {
		var s = [];
		for (var i = 0; i < this.items.length; i++) {
			if (this.items[i].isVisible()) {
				s.push(this.items[i]);
			}
		}
		return s;
	},
	getValue : function() {
		var r = '';
		for (var i = 0; i < this.items.length; i++) {
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
		for (var i = 0; i < this.items.length; i++) {
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
		else if (typeOf(v) == 'number')
			v = [ v ];
		for (var i = 0; i < v.length; i++) {
			var o = this.find(v[i]);
			if (o)
				o.setStatus('check', true);
		}
		delete this.$notUpdateInput;
		this.updateInput();
	},
	resetItems : function(items) {
		this.clearItems();
		this.addItems(items);
	},
	disposeItem : function(item) {
		item.panel.dispose();
	},
	injectItem : function(item) {
		var s = this.items, i = s.indexOf(item) + 1;
		for (; i < s.length; i++) {
			if (s[i].isVisible()) {
				item.panel.inject(s[i].panel, 'before');
				return;
			}
		}
		item.panel.inject(this.listPanel);
	},
	addItem : function(s, parentItem) {
		var p = Object.clone(this.options['listItemParams']);
		p['data'] = s;
		p['haschild'] = s['items'] != undefined;
		var r = this.createItem(p, parentItem), self = this;
		r.parentNode = parentItem;
		if (parentItem)
			r.level = parentItem.level + 1;
		else
			r.level = 0;
		r.list = this;
		r.addEvents({
			'click' : function() {
				self.itemClick(this);
			},
			'statuschanged' : function(s) {
				self.itemStatusChanged(this, s);
			}
		});
		if (parentItem)
			parentItem.items.push(r);
		this.items.push(r);
		if (p['haschild'])
			this.addItems(s['items'], r);
		if (this.identLeft == undefined)
			this.identLeft = r.getContentPanel().getMargins().left;
		if (r.level > 0)
			r.setLevelWidth(r.level * this.identLeft);
		return r;
	},
	addItems : function(items, parentItem) {
		if (parentItem && typeOf(parentItem.items) != 'array') {
			parentItem.items = [];
		}
		for (var i = 0; i < items.length; i++) {
			this.addItem(items[i], parentItem);
		}
		this.visibleItemsChange();
		if (this.valueInput && !parentItem)
			this.setValue(this.valueInput.get('value'));
		if (this.nextButtonPanel && this.items.length < this.recordCount)
			this.nextButtonPanel.inject(this.listPanel, 'after');
		this.fireEvent('itemsChanged');
	},
	createItem : function(options, p) {
		options['type'] = this.options['multiselect'] ? 'check' : 'radio';
		return new this.options['listItemClass'](options).inject(p ? p.getChildPanel() : this.listPanel);
	},
	itemClick : function(item) {
		this.fireEvent('itemClick', item);
	},
	visibleItemsChange : function() {
		var v = (this.loadingItem && this.loadingItem.isVisible());
		if (!v)
			v = this.getVisibleItems().length > 0;
		if (this.emptyItem)
			this.emptyItem.setStyle('display', v ? 'none' : '');
	},
	itemStatusChanged : function(item, s) {
		this.fireEvent('itemStatusChanged', {
			'item' : item,
			'status' : s
		});
	}
});
var UIComboBox = new Class({
	Extends : UIComboInput,
	options : {
		'list' : {
			'createParams' : {
				'class' : 'k_list'
			},
			initload : false
		},
		'windowParams' : {
			'bounds' : {
				'target' : {}
			}
		},
		'autocomplete' : false
	},
	cache : {},
	create : function(options) {
		this.parent(options);
		this.getValueInput();
		this.setText(this.list.getText());
		this.itemsChanged();
	},
	createWindow : function(options) {
		var listOptions = Object.clone(options['list']);
		var panel = listOptions['panel'] = new Element('div'), self = this;
		listOptions['input'] = this.getValueInput();
		this.list = new UIListControl(listOptions);
		this.list.addEvents({
			'itemClick' : function(item) {
				self.itemClick(item);
			},
			'valueChanged' : function() {
				self.valueChanged();
			},
			'itemsChanged' : function() {
				self.itemsChanged();
			},
			'loadSuccess' : function(e) {
				self.loadSuccess(e);
			},
			itemStatusChanged : function(e) {
				self.itemStatusChanged(e);
			}
		});
		var p = options['windowParams'];
		p['content'] = panel;
		p['bounds']['target']['element'] = this.panel;
		this.window = new UIWindow(options['windowParams']);
	},
	itemStatusChanged : function(e) {
		if (this.options['autocomplete'] && e.status == 'over' && e.item.getStatus('over')) {
			if (this.$overItem != e.item) {
				if (this.$overItem)
					this.$overItem.setStatus('over', false);
			}
			this.$overItem = e.item;
		}
	},
	loadSuccess : function(e) {
		if (this.options['autocomplete']) {
			var k = this.list.loadSendData['keyword'];
			if (k == undefined)
				k = '';
			this.cache[k] = e.data.items;
			if (this.loadDropdown)
				this.dropdown();
			delete this.loadDropdown;
		}
	},
	setText : function(v) {
		if (!this.$notSetText) {
			this.panel.set('title', v);
			this.parent(v);
		}
	},
	itemClick : function(item) {
		if (!this.options['list']['multiselect']) {
			this.selected = item;
			this.setText(item.getText());
			item.setStatus('over', false);
			this.collapse();
		}
		this.fireEvent('itemClck', item);
	},
	valueChanged : function() {
		this.setText(this.list.getText());
		this.fireEvent('valueChanged');
	},
	itemsChanged : function() {
		if (this.window.isShowing())
			this.window.resetBounds(true);
		this.fireEvent('itemsChanged');
	},
	windowShow : function() {
		this.parent();
		var s = this.list.items;
		for (var i = 0; i < s.length; i++) {
			s[i].setStatus('over', false);
		}
	},
	find : function(v, a, up, down) {
		var s = this.list.getVisibleItems(), index = (up || down) ? 0 : -1;
		for (var i = 0; i < s.length; i++) {
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
		for (var i = 0; i < ss.length; i++)
			ss[i].setStatus('check', false);
	},
	autoCompleteKeyUp : function(e) {
		if (UIInput.isCharKeyEvent(e) || e.key == 'backspace' || e.key == 'delete') {
			this.$overItem = null;
			var t = this.$savedText = this.getText(), items = this.cache[t];
			if (items) {
				this.list.resetItems(items);
			} else {
				this.loadDropdown = true;
				this.list.load({
					'keyword' : t
				});
			}
			if (this.textInput.get('value').length > 0)
				this.dropdown();
			else
				this.collapse();
		} else if (e.key == 'up' || e.key == 'down') {
			var s = this.list.getVisibleItems(), old = this.$overItem, index = !old ? -1 : s.indexOf(old);
			if (s.length > 0) {
				if (index < 0)
					index = e.key == 'up' ? s.length - 1 : 0;
				else
					index = e.key == 'up' ? index - 1 : index + 1;
				if (old)
					old.setStatus('over', false);
				old = null;
				if (index >= 0 && index < s.length) {
					(old = s[index]).setStatus('over', true);
					this.textInput.set('value', old.options['data'].text);
				} else
					this.textInput.set('value', this.$savedText);
				this.$overItem = old;
			}
			e.stop();
		} else if (e.key == 'enter') {
			this.collapse();
			delete this.$overItem;
		}
	},
	keyUp : function(e) {
		this.parent(e);
		this.$notSetText = true;
		if (this.options['autocomplete']) {
			this.autoCompleteKeyUp(e);
		} else {
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
					this.panel.set('title', t);
					this.textInput.setSelectRange(v.length, t.length);
				}
			}
		}
		delete this.$notSetText;
	},
	autoCompleteKeyDown : function(e) {
		if (e.key == 'up' || e.key == 'down')
			e.stop();
	},
	keyDown : function(e) {
		this.parent(e);
		this.$notSetText = true;
		if (!this.options['autocomplete']) {
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
					this.panel.set('title', t);
					this.textInput.setSelectRange(v.length, t.length);
				}
			}
		} else {
			this.autoCompleteKeyDown(e);
		}
		delete this.$notSetText;
	}
});
var UIChosenButton = new Class({
	Extends : UIButton,
	options : {
		'createParams' : {
			'class' : 'inline_block k_chosen_btn'
		},
		'buttonParams' : {
			'createParams' : {
				'class' : 'close_btn'
			},
			'containerParams' : undefined,
			'labelParams' : {
				'class' : 'close_btn_l'
			},
			'classPrefix' : 'close_btn'
		},
		'classPrefix' : 'k_chosen_btn',
		'type' : 'check'
	},
	doCreate : function(options) {
		this.parent(options);
		this.createButton(options);
	},
	createButton : function(options) {
		var self = this;
		this.button = new UIButton(options['buttonParams']).inject(this.panel).addEvent('click', function() {
			self.fireEvent('closeClick');
		});
	}
});
var UIChosenItem = new Class({
	Extends : UIListItem,
	filtered : false,
	options : {
		'type' : 'check'
	},
	statusChanged : function(t) {
		this.parent(t);
		if (t == 'check') {
			if (this.getStatus(t) || this.filtered) {
				this.list.disposeItem(this);
			} else {
				this.list.injectItem(this);
			}
		}
	},
	setFiltered : function(v) {
		this.filtered = v;
		if (this.getStatus('check') || v) {
			this.list.disposeItem(this);
		} else {
			this.list.injectItem(this);
		}
	}
});
var UIChosenBox = new Class({
	Extends : UIComboBox,
	$oldfilter : '',
	options : {
		'list' : {
			'createParams' : {
				'class' : 'k_list'
			},
			'listItemParams' : {},
			'listItemClass' : UIChosenItem
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
	doCreate : function(options) {
		this.parent(options);
		var s = options['value'];
		if (s != undefined) {
			if (typeOf(s) != 'array')
				s = [ s ];
			for (var i = 0; i < s.length; i++)
				this.createChosenButton(s[i]);
		}
		var calcDiv = this.calcDiv = new Element('div').inject(new Element('div').setStyles({
			'position' : 'absolute',
			'width' : '0px',
			'overflow' : 'hidden',
			'white-space' : 'nowrap'
		}).inject(this.panel)).setStyle('width', '100%');
		this.calcSpan = new Element('span', options['textInputParams']).inject(calcDiv);
	},
	createDropImg : function(options) {
	},
	setText : function(v) {
	},
	createButton : function() {
	},
	resizeInput : function() {
		var v = this.textInput.get('value');
		this.calcSpan.set('text', v);
		var x = this.calcSpan.getSize().x, s = this.panel.getComputedSize();
		if (x > s.width)
			x = s.width;
		this.textInput.setStyle('width', x + 30);
	},
	keyUp : function(e) {
		if (e.key == 'enter')
			return;
		if (this.options['autocomplete']) {
			this.autoCompleteKeyUp(e);
			this.resizeInput();
		} else {
			var v = this.textInput.get('value');
			this.resizeInput();
			this.filter(v);
			if (this.window.isShowing()) {
				if (UIInput.isCharKeyEvent(e) || e.key == 'backspace')
					this.window.resetBounds(true);
			} else {
				this.dropdown();
				if (this.$overItem)
					this.$overItem.setStatus('over', true);
			}
		}
	},
	keyDown : function(e) {
		if (e.key == 'tab')
			this.collapse();
		else if (e.key == 'down' || e.key == 'up') {
			e.stop();
			if (this.options['autocomplete'])
				return;
			var sel = this.$overItem, s = this.list.getVisibleItems(), index = s.indexOf(sel);
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
			this.$overItem = sel;
		} else {
			if (e.key == 'enter')
				if (this.$overItem) {
					this.$savedText = '';
					this.itemClick(this.$overItem);
					if (this.options['autocomplete']) {
						var items = this.cache[''];
						if (items) {
							this.list.resetItems(items);
							this.dropdown();
						} else {
							this.loadDropdown = true;
							this.list.load({
								'keyword' : ''
							});
						}
					}
				}
			if (this.$overItem) {
				delete this.$overItem;
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
		delete this.$overItem;
		this.$oldfilter = str;
		var s = this.list.items;
		for (var i = 0; i < s.length; i++) {
			s[i].setStatus('over', false);
			s[i].setFiltered(str != '' && !s[i].getText().contains(str));
		}
		this.list.visibleItemsChange();
	},
	createChosenButton : function(item) {
		var ps = Object.clone(this.options['chosenButtonParams']), self = this;
		ps['panel'] = new Element('div').inject(this.textInput, 'before');
		ps['labelParams']['text'] = item.text;
		var b = new UIChosenButton(ps).addEvents({
			'closeClick' : function() {
				self.closeClick.call(self, this);
			}
		});
		b.data = item;
		this.selItems.push(b);
	},
	clearSelected : function() {
		var s = this.selItems;
		for (var i = 0; i < s.length; i++) {
			if (s[i].status['check'])
				s[i].setStatus('check', false);
		}
	},
	removeSelected : function() {
		var s = this.selItems, r = false;
		for (var i = 0; i < s.length; i++) {
			if (s[i].status['check']) {
				this.closeClick(s[i]);
				r = true;
				i--;
			}
		}
		return r;
	},
	closeClick : function(item) {
		var li = this.list.find(item.data.value || item.data.id);
		if (li)
			li.setStatus('check', false);
		item.panel.dispose();
		this.selItems.erase(item);
		this.collapse();
		this.list.visibleItemsChange();
	},
	itemClick : function(item) {
		this.createChosenButton(item.options.data);
		item.setStatus('check', true);
		item.setStatus('over', false);
		this.filter('');
		this.list.visibleItemsChange();
		this.collapse();
		this.textInput.set('value', '');
		this.textInput.focus();
	},
	itemsChanged : function() {
		for (var i = 0; i < this.selItems.length; i++) {
			var d = this.selItems[i].data, item = this.list.find(d.value || d.id);
			if (item != null)
				item.setStatus('check', true);
		}
		this.list.visibleItemsChange();
		this.parent();
	},
	click : function(e) {
		if (e.target == this.container)
			this.textInput.focus();
		this.buttonClick(e);
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
			'value' : '2012-01-01'
		},
		'upParams' : {
			'createParams' : {
				'class' : 'up'
			},
			'containerParams' : undefined,
			'labelParams' : {
				'class' : 'up_btn',
				'text' : ' '
			},
			'classPrefix' : 'up',
			'mouseDownClicks' : true
		},
		'downParams' : {
			'createParams' : {
				'class' : 'down'
			},
			'containerParams' : undefined,
			'labelParams' : {
				'class' : 'down_btn',
				'text' : ' '
			},
			'classPrefix' : 'down',
			'mouseDownClicks' : true
		},
		'masks' : ''
	},
	doCreate : function(options) {
		this.masks = options['masks'].decodeMasks();
		this.parent(options);
		this.createUpDown(options);
	},
	createUpDown : function(options) {
		var div = new Element('div', options['updownParams']).inject(this.container).setSelectable(false), self = this;
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
		'createParams' : {
			'class' : 'inline_block k_input k_timeedit'
		},
		'inputParams' : {
			value : '00:00:00',
			'maxlength' : 8
		},
		'masks' : '0-2,0-23|3-5,0-59|6-8,0-59'
	}
});
var UICalendar = new Class({
	Extends : UIControl,
	options : {
		'createParams' : {
			'class' : 'inline_block border k_calendar'
		},
		'buttonParams' : {
			'createParams' : {
				'class' : 'inline_block calc_btn'
			},
			'classPrefix' : 'calc_btn',
			'labelParams' : {}
		}
	},
	today : new Date(),
	doCreate : function(options) {
		this.parent(options);
		this.panel.checkInlineBlock();
		this.getValueInput();
		var v = options['value'] || this.valueInput.get('value');
		this.day = v.length == 0 ? this.today : v.toDate();
		this.startDate = options['startdate'] != undefined ? options['startdate'].toDate() : null;
		this.endDate = options['enddate'] != undefined ? options['enddate'].toDate() : null;
		this.hour = this.day.getHours();
		this.min = this.day.getMinutes();
		this.sec = this.day.getSeconds();
		this.createTitlePanel(options);
		this.dayCell = this.createDayCell(options);
		this.monthCell = this.createCell12('m');
		this.yearCell = this.createCell12('y');
		this.yyCell = this.createCell12('yy');
		this.createTimePanel();
		if (typeOf(options['startcalendar']) == 'object') {
			this.setStartCalendar(options['startcalendar']);
			options['startcalendar'].setEndCalendar(this);
		}
		this.fillCell();
		this.change(false);
	},
	setDay : function(day) {
		var td = new Date(this.day.getTime());
		td.setHours(0, 0, 0, 0);
		var d = new Date(day.getTime());
		d.setHours(0, 0, 0, 0);
		var s = this.startDate, e = this.endDate;
		if (s && s.getFullDay() > day.getFullDay()) {
			d = s;
		}
		if (e && e.getFullDay() < day.getFullDay())
			d = e;
		d.setHours(0, 0, 0, 0);
		if (td.getFullDay() == d.getFullDay())
			return false;
		this.day = d;
		this.fillCell();
		if (this.startCalendar != undefined) {
			if (this.day.getFullDay() < this.startCalendar.day.getFullDay())
				this.startCalendar.setDay(day);
		}
		if (this.endCalendar != undefined) {
			if (this.day.getFullDay() > this.endCalendar.day.getFullDay())
				this.endCalendar.setDay(day);
		}
		this.change(true);
		return true;
	},
	getValue : function() {
		if (this.options['timeselectenabled']) {
			return this.day.getDateString() + ' ' + this.timeEdit.getValue();
		} else
			return this.day.getDateString();
	},
	setValue : function(v) {
		if (this.valueInput)
			this.valueInput.set('value', v);
		this.fireEvent('setValue', v);
	},
	buttonClick : function(item) {
		var day = this.day;
		if (item.cmd == 'd' || item.cmd == 'today') {
			day = item.cmd == 'today' ? this.today : item.data;
			if (!this.dayCell.panel.isVisible())
				this.dayCell.panel.inject(this.titlePanel, 'after');
			this.monthCell.panel.dispose();
			this.yearCell.panel.dispose();
			this.yyCell.panel.dispose();
			if (day.getFullMonth() == this.day.getFullMonth()) {
				if (this.daySel != null)
					this.daySel.panel.removeClass('calc_sel');
				this.daySel = item.cmd != 'today' ? item : this.daySels[day.getFullDay()];
				this.daySel.panel.addClass('calc_sel');
			}
			if (item.cmd == 'd')
				this.fireEvent('dayClick');
		} else if (item.cmd == '<' || item.cmd == '>') {
			if (this.dayCell.panel.isVisible())
				day = this.day.add('m', item.cmd == '<' ? -1 : 1);
			else if (this.monthCell.panel.isVisible())
				day = this.day.add('y', item.cmd == '<' ? -1 : 1);
			else if (this.yearCell.panel.isVisible())
				day = this.day.add('y', item.cmd == '<' ? -10 : 10);
			else
				day = this.day.add('y', item.cmd == '<' ? -100 : 100);
		} else if (item.cmd == 'm') {
			if (this.monthSel != null)
				this.monthSel.panel.removeClass('calc_sel');
			day = item.data;
			this.monthSel = item;
			this.monthSel.panel.addClass('calc_sel');
			this.monthSel.setStatus('over', false);
			this.monthCell.panel.dispose();
			this.dayCell.panel.inject(this.titlePanel, 'after');
		} else if (item.cmd == 'y') {
			if (this.yearSel != null)
				this.yearSel.panel.removeClass('calc_sel');
			day = item.data;
			this.yearSel = item;
			this.yearSel.panel.addClass('calc_sel');
			this.yearSel.setStatus('over', false);
			this.yearCell.panel.dispose();
			this.monthCell.panel.inject(this.titlePanel, 'after');
		} else if (item.cmd == 'yy') {
			if (this.yySel != null)
				this.yySel.panel.removeClass('calc_sel');
			day = new Date(this.day.getTime());
			day.setFullYear(parseInt(item.data / 10) * 10 + day.getFullYear() % 10, day.getMonth(), day.getDate());
			this.yySel = item;
			this.yySel.panel.addClass('calc_sel');
			this.yyCell.panel.dispose();
			this.yySel.setStatus('over', false);
			this.yearCell.panel.inject(this.titlePanel, 'after');
		} else if (item.cmd == 'title') {
			if (this.dayCell.panel.isVisible()) {
				this.dayCell.panel.dispose();
				this.monthCell.panel.inject(this.titlePanel, 'after');
			} else if (this.monthCell.panel.isVisible()) {
				this.monthCell.panel.dispose();
				this.yearCell.panel.inject(this.titlePanel, 'after');
			} else if (this.yearCell.panel.isVisible()) {
				this.yearCell.panel.dispose();
				this.yyCell.panel.inject(this.titlePanel, 'after');
			}
			this.fillTitle();
			return;
		}
		if (this.day.getFullDay() != day.getFullDay())
			this.setDay(day);
		else
			this.fillTitle();
	},
	createButton : function(p, t, cmd, c) {
		var r = new UIButton(this.options['buttonParams']), self = this;
		r.setCaption(t);
		r.panel.inject(p);
		r.panel.checkInlineBlock();
		r.addEvents({
			'click' : function() {
				self.buttonClick.call(self, this);
			}
		});
		if (c)
			r.panel.addClass(c);
		r.cmd = cmd;
		return r;
	},
	createTitlePanel : function(options) {
		var d = this.titlePanel = new Element('div', {
			'class' : 'cale_t'
		}).inject(this.panel);
		this.createButton(d, '<', '<', 'cale_w1');
		this.titleButton = this.createButton(d, '2010', 'title', 'cale_w2');
		this.createButton(d, '>', '>', 'cale_w1');
		this.createButton(d, '今天', 'today', 'cale_w3');
	},
	createTimePanel : function(options) {
		if (this.options['timeselectenabled']) {
			var d = this.timeDiv = new Element('div', {
				'class' : 'cale_time'
			}).inject(this.panel);
			new Element('div', {
				'class' : 'inline_block cale_time_l',
				'text' : '时间：'
			}).inject(d).checkInlineBlock();
			var self = this;
			this.timeEdit = new UITimeEdit({
				'panel' : new Element('div').inject(d),
				'inputParams' : {
					'value' : this.day.getTimeString()
				},
				'events' : {
					'change' : function() {
						self.setValue(self.getValue());
					}
				}
			});
		}
	},
	change : function(fireEvent) {
		this.day.setHours(this.hour, this.min, this.sec, 0);
		this.setValue(this.getValue());
		if (this.startCalendar)
			this.startCalendar.fillCell();
		if (this.endCalendar)
			this.endCalendar.fillCell();
		if (fireEvent)
			this.fireEvent('change', this);
	},
	setStartCalendar : function(c) {
		this.startCalendar = c;
		this.fillCell();
	},
	setEndCalendar : function(c) {
		this.endCalendar = c;
		this.fillCell();
	},
	createCell12 : function(cmd) {
		var p = new Element('div').inject(this.panel), cells = [], hc = cmd == 'yy' ? 'cale_hyy' : 'cale_h12';
		for (var i = 0; i < 3; i++) {
			cells.push([]);
			d = new Element('div').inject(p);
			for (var j = 0; j < 4; j++) {
				var b = this.createButton(d, cmd, cmd, 'cale_w12 ' + hc);
				cells[i].push(b);
				b.panel.checkInlineBlock();
			}
		}
		p.dispose();
		return {
			'panel' : p,
			'cells' : cells,
			'cmd' : cmd
		};
	},
	createDayCell : function() {
		var p = new Element('div').inject(this.panel);
		var t = [ '日', '一', '二', '三', '四', '五', '六' ];
		var d = new Element('div', {
			'class' : 'calc_week'
		}).inject(p);
		var cells = [];
		for (var i = 0; i < 7; i++) {
			new Element('div', {
				'class' : (i == 0) ? 'inline_block calc_weekred' : 'inline_block calc_weekblue'
			}).inject(d).set('text', t[i]).checkInlineBlock();
		}
		for (var i = 0; i < 6; i++) {
			cells.push([]);
			d = new Element('div').inject(p);
			for (var j = 0; j < 7; j++) {
				cells[i].push(this.createButton(d, '20', 'd', 'cale_w1'));
			}
		}
		return {
			'panel' : p,
			'cmd' : 'd',
			'cells' : cells
		};
	},
	isCellEnabled : function(o, d, t) {
		var s = this.startDate, e = this.endDate;
		if (t == 'd') {
			return (!s || d.getFullDay() >= s.getFullDay()) && (!e || d.getFullDay() <= e.getFullDay());
		} else if (t == 'm')
			return (!s || d.getFullMonth() >= s.getFullMonth()) && (!e || d.getFullMonth() <= e.getFullMonth());
		else if (t == 'y') {
			return (!s || d.getFullYear() >= s.getFullYear()) && (!e || d.getFullYear() <= e.getFullYear());
		} else {
			return (!s || d >= s.getFullYear()) && (!e || d <= e.getFullYear());
		}
	},
	isCellCurPanel : function(o, d, t) {
		if (t == 'd') {
			return d.getFullMonth() == this.day.getFullMonth();
		} else if (t == 'm')
			return true;
		else if (t == 'y') {
			var y = this.day.getFullYear();
			var sy = parseInt(y / 10) * 10;
			return (d.getFullYear() != sy - 1 && d.getFullYear() != sy + 10);
		} else {
			var y = this.day.getFullYear();
			var sy = parseInt(y / 100) * 100;
			return (d != sy - 10 && d != sy + 100);
		}
	},
	isCellSelected : function(d, t) {
		var td = this.day;
		return (t == 'd' && d.getFullDay() == td.getFullDay())
				|| (t == 'm' && d.getFullMonth() == td.getFullMonth() || (t == 'y' && d.getFullYear() == td
						.getFullYear())) || (td.getFullYear() >= d && td.getFullYear() < d + 10);
	},
	setCellData : function(o, d, t) {
		var ac = [], rc = [ 'calc_dis', 'calc_sel' ];
		o.data = d;
		o.setStatus('disabled', !this.isCellEnabled(o, d, t));
		if (!this.isCellCurPanel(o, d, t))
			ac.push('calc_dis');
		if (this.isCellSelected(d, t))
			ac.push('calc_sel');
		if (t == 'd') {
			if (d.getFullDay() == this.today.getFullDay())
				ac.push('calc_today');
			rc.push('calc_today');
		}
		o.panel.addRemoveClass(ac, rc.eraseAll(ac));
	},
	fillDayCell : function() {
		var cells = this.dayCell.cells;
		var o = new Date(this.day.getTime());
		o.setHours(0, 0, 0, 0);
		o.setDate(1);
		if (o.getDay() != 0)
			o = o.add('d', -o.getDay());
		else
			o = o.add('d', -7);
		this.daySel = null;
		this.daySels = {};
		for (var i = 0; i < 6; i++) {
			for (var j = 0; j < 7; j++) {
				var e = cells[i][j];
				e.setCaption(o.getDate());
				e.setStatus('over', false);
				this.daySels[o.getFullDay()] = e;
				this.setCellData(e, o, 'd');
				if (o.getFullDay() == this.day.getFullDay()) {
					this.daySel = e;
				}
				o = o.add('d', 1);
			}
		}
		var d = new Date(this.day.getTime());
		var y = d.getFullYear();
		var sy = parseInt(y / 10) * 10 - 1;
		var syy = parseInt(y / 100) * 100 - 10;
		for (var i = 0; i < 3; i++) {
			for (var j = 0; j < 4; j++) {
				var o = this.monthCell.cells[i][j], m = (i * 4 + j), y = m + sy, d = new Date(this.day.getTime());
				d.setMonth(m, d.getDate());
				this.setCellData(o, d, 'm');
				if (o.panel.hasClass('clac_sel'))
					this.monthSel = item;
				o.setCaption(Date.CH_MONTHS[m]);
				o = this.yearCell.cells[i][j];
				if (o.panel.hasClass('clac_sel'))
					this.yearSel = item;
				d = new Date(this.day.getTime());
				d.setFullYear(y, d.getMonth(), d.getDate());
				this.setCellData(o, d, 'y');
				o.setCaption(y);
				o = this.yyCell.cells[i][j];
				d = syy;
				this.setCellData(o, d, 'yy');
				if (o.panel.hasClass('clac_sel'))
					this.yySel = item;
				d = new Date(this.day.getTime());
				o.setCaption(syy + '-' + (syy + 9));
				syy += 10;
			}
		}
	},
	fillTitle : function() {
		var s = '';
		if (this.dayCell.panel.isVisible())
			s = this.day.getMonthString();
		else if (this.monthCell.panel.isVisible())
			s = this.day.getFullYear();
		else if (this.yearCell.panel.isVisible()) {
			var y = parseInt(this.day.getFullYear() / 10) * 10;
			s = y + '-' + (y + 9);
		} else {
			var y = parseInt(this.day.getFullYear() / 100) * 100;
			s = y + '-' + (y + 99);
		}
		this.titleButton.setCaption(s);
	},
	fillCell : function() {
		this.fillDayCell();
		this.fillTitle();
	}
});
var UICalendarBox = new Class({
	Extends : UIComboInput,
	options : {
		'buttonParams' : {
			'labelParams' : {
				'class' : 'cale_btn_l'
			}
		},
		'createParams' : {
			'date_class' : 'k_date_edit',
			'datetime_class' : 'k_datetime_edit'
		},
		'calendar' : {
			'createParams' : {
				'class' : 'k_calendar'
			}
		},
		'windowParams' : {
			'bounds' : {
				'target' : {}
			}
		},
		'masks' : '0-4,0-0|5-7,1-12|8-10,1-31'
	},
	getCaretMask : function() {
		var m1 = this.masks[0], m2 = this.masks[2], s = this.startDate, e = this.endDate;
		m2.max = this.calendar.day.getMonthDays();
		if (s)
			m1.min = s.getFullYear();
		if (e)
			m1.max = e.getFullYear();
		else
			delete m1.max;
		return this.parent();
	},
	doCreate : function(options) {
		var t = options['calendar']['timeselectenabled'];
		if (t)
			options['masks'] = '0-4,0-0|5-7,1-12|8-10,1-31|11-13,0-23|14-16,0-59|17-19,0-59';
		this.parent(options);
		if (!t)
			this.panel.addClass(options['createParams']['date_class']);
		else
			this.panel.addClass(options['createParams']['datetime_class']);
	},
	setText : function(t) {
		this.parent(t);
		if (this.valueInput)
			this.valueInput.set('value', t);
	},
	createWindow : function(options) {
		var calOptions = Object.clone(options['calendar']);
		var panel = calOptions['panel'] = new Element('div').inject(this.panel), self = this;
		calOptions['input'] = this.getValueInput();
		this.calendar = new UICalendar(calOptions);
		panel.dispose();
		this.calendar.addEvents({
			'setValue' : function(v) {
				self.setText(v);
			},
			dayClick : function() {
				self.collapse();
			}
		});
		var p = options['windowParams'];
		p['content'] = panel;
		p['bounds']['target']['element'] = this.panel;
		this.window = new UIWindow(options['windowParams']);
		this.setText(this.calendar.getValue());
	},
	change : function() {
		this.parent();
		var t = this.getText();
		var t1 = this.calendar.getValue();
		if (t.substr(0, 10) == t1.substr(0, 10))
			this.calendar.timeEdit.setText(t.substring(11));
		else {
			this.calendar.setDay(t.toDate());
		}
		var str = this.calendar.getValue();
		this.setText(str);
	},
	setStartCalendar : function(c) {
		this.calendar.setStartCalendar(c.calendar);
	},
	setEndCalendar : function(c) {
		this.calendar.setEndCalendar(c.calendar);
	}
});
var UITitleWindow = new Class({
	Extends : UIModelWindow,
	options : {
		'titleParams' : {
			'class' : 'win_title',
			'text' : '',
			'text_class' : 'title_text',
			'buttons' : {
				'class' : 'win_btn_panel',
				'min' : {
					'class' : 'inline_block win_btn win_min'
				},
				'max' : {
					'class' : 'inline_block win_btn win_max',
					'class1' : 'win_max',
					'class2' : 'win_max1'
				},
				'close' : {
					'class' : 'inline_block win_btn win_close'
				}
			}
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.createTitle(options);
	},
	createTitle : function(options) {
		var ps = options['titleParams'], self = this, mouseUp = null, downPos = undefined;
		var p = this.titlePanel = new Element('div', {
			'class' : ps['class']
		}).inject(this.content, 'before'), bs = ps['buttons'], docEL = document.documentElement;
		var mouseMove = function(e) {
			var pos = Dimension.lockPosInRect(e.page, docEL.getViewBounds());
			pos = {
				x : pos.x - downPos.x,
				y : pos.y - downPos.y
			};
			self.panel.setPosition(pos);
		};
		mouseUp = function(e) {
			downPos = undefined;
			docEL.removeEvents({
				'mousemove' : mouseMove,
				'mouseup' : mouseUp
			});
		};
		this.title = new Element('div', {
			'class' : ps['text_class'],
			'text' : ps['text'] || '',
			events : {
				'mousedown' : function(e) {
					if (!e.rightClick) {
						e.stop();
						docEL.fireEvent('mousedown', e);
					} else
						return;
					if (downPos)
						return;
					downPos = Dimension.getRelativePos(e.page, self.panel.getBounds());
					docEL.addEvents({
						'mousemove' : mouseMove,
						'mouseup' : mouseUp
					});
				}
			}
		}).inject(p).setSelectable(false);
		if (ps['html'])
			this.title.set('html', ps['html']);
		if (bs != undefined) {
			var bp = new Element('div', {
				'class' : bs['class']
			}).inject(p);
			var bc = function(n) {
				if (bs[n] != undefined) {
					new UIButton({
						'createParams' : bs[n],
						'parent' : bp,
						'events' : {
							'click' : function(e) {
								switch (this.type) {
								case 'max':
									self.maxClick(this);
									break;
								case 'min':
									self.minClick(this);
									break;
								case 'close':
									self.closeClick(this);
									break;
								}
							}
						}
					}).type = n;
				}
			};
			bc('min');
			bc('max');
			bc('close');
		}
	},
	maxClick : function(o) {
		var de = document.documentElement, s = o.options.createParams;
		if (this.maxed = !this.maxed) {
			this.$savedBounds = this.panel.getBounds();
			this.panel.setBounds(de.getViewBounds());
			o.panel.addRemoveClass(s['class2'], s['class1']);
		} else {
			this.panel.setBounds(this.$savedBounds);
			o.panel.addRemoveClass(s['class1'], s['class2']);
			delete this.$savedBounds;
		}
	},
	minClick : function(o) {
		if (this.mined = !this.mined) {
			this.$savedHeight = this.panel.getStyle('height');
			this.panel.tween('height', this.titlePanel.getSize().y);
		} else {
			this.panel.tween('height', this.$savedHeight);
			delete this.$savedHeight;
		}
	},
	closeClick : function(o) {
		this.hide();
	}
});
var UIGroup = new Class({
	Extends : UIControl,
	'options' : {
		'labelParams' : {
			'class' : 'grouplabel'
		},
		'createParams' : {
			'class' : 'inline_block k_group'
		}
	},
	doCreate : function(options) {
		var c = $(options['panel']);
		if (!c) {
			c = new Element('div').inject($(options['parent']));
		}
		var f = new Element('div', options['createParams']).inject(c, 'before');
		c.inject(f);
		this.label = new Element('label', options['labelParams']).inject(new Element('div', {
			'class' : 'labelparent'
		}).inject(c, 'before'));
		this.panel = f;
	}
});
var UIPageControl = new Class({
	Extends : UIControl,
	'options' : {
		'createParams' : {
			'class' : 'k_pagecontrol'
		},
		'buttonPanelParams' : {
			'class' : 'buttonpanel'
		},
		'contentPanelParams' : {
			'class' : 'pagecontent'
		},
		'buttonParams' : {
			'createParams' : {
				'class' : 'inline_block pagebutton'
			},
			'classPrefix' : 'pagebutton',
			'type' : 'radio'
		},
		'pageParams' : {
			'class' : 'page'
		},
		'spaceButtonParams' : {
			'class' : 'inline_block pagebuttonspace'
		}
	},
	doCreate : function(options) {
		this.parent(options);
		this.buttonPanel = $(options['buttonpanel']) || new Element('div', options['buttonPanelParams']);
		this.content = $(options['content']) || new Element('div', options['contentPanelParams']);
		this.buttonPanel.inject(this.panel);
		this.content.inject(this.panel);
		this.pages = [];
		var ps = this.options;
		var items = ps['items'];
		this.groupdef = String.uniqueID();
		for (var i = 0; i < items.length; i++) {
			var o = items[i];
			var a = this.createPage(o);
			if (o['selected'])
				this.current = a;
			if (i < items.length - 1) {
				new Element('div', ps['spaceButtonParams']).set('html', '&nbsp;').inject(this.buttonPanel);
			}
			this.pages.push(a);
		}
		if (this.current)
			this.current.setStatus('check', true);
	},
	createPage : function(p) {
		var b = Object.merge(this.options['buttonParams'], p['button']);
		var c = p['content'];
		if (typeOf(c) != 'element') {
			c = Object.merge(this.options['pageParams'], c);
			c = new Element('div', c);
		}
		var _this = this;
		var r = new UIButton(Object.merge(b, {
			'parent' : this.buttonPanel,
			'group' : this.groupdef,
			'events' : {
				'statuschanged' : function(s) {
					if (s == 'check' && this.getStatus('check'))
						_this.pageChanged(this);
				}
			}
		}));
		r.content = c;
		r.url = p['url'];
		r.jsCssFiles = p['jsCssFiles'];
		return r;
	},
	pageChanged : function(o) {
		var old = this.current, disposed = !this.options['hide_on_changed'];
		var _this = this;
		var ed = function(b) {
			for (var i = 0; i < _this.pages.length; i++) {
				if (_this.pages[i] != o)
					_this.pages[i].setStatus('disabled', b);
			}
		};
		var ej = function() {
			if (old && old != o) {
				if (disposed) {
					old.content.dispose();
					if (old.jsCssFiles)
						_this.options['jsCssFileMgr'].clean(old.jsCssFiles);
				} else
					old.content.setStyle('display', 'none');
			}
			o.content.inject($(_this.content));
			if (o.content.getStyle('display') == 'none')
				o.content.setStyle('display', '');
		};
		this.current = o;
		if (o.url) {
			ed(true);
			if (this.options['isleftright']) {
				var y = this.buttonPanel.getPosition().y;
				var y1 = this.content.getPosition().y;
				this.content.setMinHeight(this.buttonPanel.getComputedSize().totalHeight + y - y1 + 10);
			}
			o.content.dynamicLoad({
				url : o.url,
				events : {
					'prepareUpdate' : function() {
						ej();
					},
					'complete' : function() {
						ed(false);
					}
				},
				jsCssFileMgr : this.options['jsCssFileMgr'],
				jsCssFiles : o.jsCssFiles
			});
		} else {
			ej();
		}
		this.fireEvent('pageChanged');
	},
	findById : function(id) {
		for (var i = 0; i < this.pages.length; i++) {
			var o = this.pages[i];
			if (o.ps['id'] == id)
				return o;
		}
		return undefined;
	}
});
var UITreeItem = new Class({
	Extends : UIListItem,
	options : {
		'createParams' : {
			'class' : 'k_treeitem'
		},
		'classPrefix' : 'k_treeitem',
		'labelParams' : {
			'class' : 'tilabel'
		},
		'expanded' : false,
		'haschild' : false
	},
	doCreate : function(options) {
		var b = options['expanded'], self = this;
		this.parent(options);
		var c = this.container;
		this.captionLabel.inject(c);
		if (options['haschild'] || options['data']['needload']) {
			this.panel.addRemoveClass(!b ? 'collapse' : 'expand', b ? 'collapse' : 'expand');
			this.eb = new Element('b').inject(c, 'top').addEvents({
				'click' : function(e) {
					if (self.expanded)
						self.collapse();
					else
						self.expand();
					e.stop();
				},
				'mousedown' : function(e) {
					e.stop();
				}
			});
			if (options['haschild'] && b)
				this.expand();
		}
		if (options['checkboxes']) {
			new Element('span', {
				'class' : 'treecheckbox'
			}).inject(c);
		}
	},
	inject : function(o, p) {
		this.parent(o, p);
		if (this.expanded && this.getChildPanel().getParent() == null)
			this.getChildPanel().inject(this.panel, 'after');
		return this;
	},
	expand : function() {
		if (!this.expanded) {
			var b = this.expanded = true;
			this.panel.addRemoveClass(!b ? 'collapse' : 'expand', b ? 'collapse' : 'expand');
			this.getChildPanel().inject(this.panel, 'after');
			var d = this.options['data'];
			if (d['needload'] && !this.isload) {
				var w = this.container.getMargins().left + this.captionLabel.getMargins().left * 2;
				var el = this.loadingpanel = new Element('div', {
					'text' : '载入中...',
					'class' : 'loading_item',
					styles : {
						'margin-left' : w + 'px'
					}
				}).inject(this.getChildPanel());
				this.isload = true;
				this.tree.load({
					id : d['value']
				}, this);
			}
		}
	},
	collapse : function() {
		if (this.expanded) {
			var b = this.expanded = false;
			this.panel.addRemoveClass(!b ? 'collapse' : 'expand', b ? 'collapse' : 'expand');
			this.getChildPanel().dispose();
		}
	},
	getChildPanel : function() {
		if (!this.childpanel)
			this.childpanel = new Element('div');
		return this.childpanel;
	},
	setLevelWidth : function(w) {
		this.container.setStyle('margin-left', w + 'px');
	}
});
var UITreeControl = new Class({
	Extends : UIListControl,
	'options' : {
		'createParams' : {
			'class' : undefined
		},
		'listPanelParams' : {
			'class' : 'k_tree'
		},
		'listItemClass' : UITreeItem,
		'emptyParams' : {
			'text' : undefined
		},
		expandDepth : 2,
		level : 0
	},
	checkAll : function(item, checked) {
		if (item.items == undefined)
			return;
		for (var i = 0; i < item.items.length; i++) {
			var it = item.items[i];
			it.setStatus('check', checked);
			if (it.items != undefined)
				this.checkAll(it, checked);
		}
	},
	addItems : function(items, parentItem) {
		if (parentItem && parentItem.loadingpanel)
			parentItem.loadingpanel.dispose();
		this.$adding = true;
		this.parent(items, parentItem);
		this.$adding = false;
	},
	createLoadingItem : function(options) {
		return undefined;
	},
	createItem : function(options, parentItem) {
		var p = this.options;
		options['checkboxes'] = p['checkboxes'];
		options['expanded'] = parentItem == undefined || parentItem.level < p['expandDepth'] - 1;
		var r = this.parent(options, parentItem);
		r.tree = this;
		return r;
	},
	itemStatusChanged : function(item, s) {
		this.parent(item, s);
		if (this.$busing || this.$adding || !this.$created || !this.options['multiselect'])
			return;
		this.$busing = true;
		if (s == 'check') {
			if (item.getStatus('check')) {
				var p = item.parentNode;
				while (p) {
					if (!p.getStatus('check'))
						p.setStatus('check', true);
					p = p.parentNode;
				}
			}
			this.checkAll(item, item.getStatus('check'));
		}
		delete this.$busing;
	}
});
var UITableItem = new Class({
	Extends : UIListItem,
	options : {
		'createParams' : {
			'class' : 'k_tablerow'
		},
		'classPrefix' : 'k_tablerow'
	},
	initialize : function(options, table) {
		this.table = table;
		this.parent(options);
	},
	getContentPanel : function() {
		return this.container;
	},
	createField : function(column) {
		var table = this.table;
		var data = this.options['data'], text = data && data[column.field] ? data[column.field] : '', r;
		var d = {
			'column' : column,
			'item' : this
		};
		this.table.fireEvent('createField', d);
		if (d.element) {
			r = d.element;
			r.inject(this.container);
		} else {
			r = new Element('div').inject(this.container);
			var rr = new Element('div').inject(r);
			var self = this;
			if (column.checkbox) {
				this.checkbox = new Element('input', {
					'type' : 'checkbox',
					'events' : {
						'click' : function(e) {
							if (this.row == table.headRow)
								table.checkAll(this.checked);
						}
					}
				}).inject(rr);
				this.checkbox.row = this;
			} else if (data) {
				rr.set('text', text);
			} else {
				rr.set('text', column.caption);
			}

			if (data) {
				rr.addClass(column.row_class);
			} else {
				rr.addClass(column.head_class);
			}
		}
		if (column.width == 'auto') {
			r.setStyles({
				'margin-left' : column.left + 'px',
				'margin-right' : column.right + 'px'
			});
		} else if (column.left) {
			r.setStyles({
				'width' : column.width + 'px',
				'position' : 'absolute',
				top : '0px',
				'left' : column.left + 'px'
			});
		} else {
			r.setStyles({
				'width' : column.width + 'px',
				'top' : '0px',
				'position' : 'absolute',
				'right' : column.right + 'px'
			});
		}
		return r;
	},
	createContent : function(options) {
		var table = this.table, ps = table.options, columns = ps['columns'];
		var isL = true;
		for (var i = 0; i < columns.length; i++) {
			var field = this.createField(columns[i]);
		}
	}
});
var UITableControl = new Class({
	Extends : UIListControl,
	'options' : {
		'createParams' : {
			'class' : 'k_table'
		},
		'listPanelParams' : {
			'class' : 'table_items'
		},
		'listItemClass' : UITableItem,
		'headParams' : {
			'createParams' : {
				'class' : 'k_tablehead'
			},
			'classPrefix' : 'k_tablehead'
		},
		'emptyParams' : {
			'text' : undefined
		}
	},
	doCreate : function(options) {
		var cs = this.options['columns'], ca, l = 0, i = 0, r = 0, self = this;
		for (; i < cs.length; i++) {
			var c = cs[i];
			if (c.width == 'auto') {
				ca = c;
				break;
			} else {
				c.left = l;
				l += c.width;
			}
		}
		for (var j = cs.length - 1; j > i; j--) {
			var c = cs[j];
			c.right = r;
			r += c.width;
		}
		if (ca) {
			ca.left = l;
			ca.right = r;
		}
		this.parent(options);
		this.headRow = this.createItem();
		if (this.headRow) {
			this.headRow.setStatus('disabled', true);
			this.updateCheckBoxes();
		}
		var p = this.pageParent = new Element('div').inject(this.panel);
		this.pageSelector = new UIPageSelector(Object.merge(options['pageSelectorParams'] || {}, {
			'parent' : p,
			'events' : {
				'change' : function() {
					self.load({
						firstindex : (self.pageSelector.page - 1) * self.options.requestData.maxresults
					});
				}
			}
		}));
	},
	getLoadingItemParent : function() {
		return this.pageParent;
	},
	loadStart : function() {
		this.parent();
		this.pageSelector.dispose();
	},
	loadError : function(e) {
		this.parent(e);
		if (this.pageSelector) {
			this.pageSelector.inject(this.pageParent);
		}
	},
	loadSuccess : function(e) {
		this.parent(e);
		if (this.pageSelector) {
			var p = this.options['requestData'];
			this.pageSelector.inject(this.pageParent);
			if (p.firstindex < 0)
				this.pageSelector.setPages(Math.ceil(this.recordCount / (p['maxresults'] || 12)), this.recordCount);
		}
	},
	add : function(o) {
		return this.addItem(o['data']);
	},
	update : function(o) {
		var id = o['data']['id'] || o['data']['value'];
		var old = this.find(id);
		var r = this.addItem(o['data']);
		if (old) {
			r.inject(old.panel, 'before');
			this.items.erase(old);
		}
		old.dispose();
	},
	createItem : function(options) {
		var h = options == undefined;
		if (!options)
			options = {};
		options = Object.merge(options, h ? this.options['headParams'] : this.options['rowParams']);
		options['type'] = this.options['multiselect'] ? 'check' : 'radio';
		var r = new UITableItem(options, this);
		if (h)
			r.inject(this.listPanel, 'top');
		else
			r.inject(this.listPanel);
		return r;
	},
	checkAll : function(checked) {
		for (var i = 0; i < this.items.length; i++) {
			this.items[i].setStatus('check', checked);
		}
		if (this.headRow)
			this.headRow.setStatus('check', checked);
	},
	updateCheckBoxes : function(o) {
		if (!this.headRow)
			return;
		if (o == this.headRow) {
			this.checkAll(checked);
		} else {
			var count = 0, all = this.headRow.checkbox;
			for (var i = 0; i < this.items.length; i++) {
				if (this.items[i].getStatus('check'))
					count++;
			}
			all.indeterminate = false;
			if (count == 0)
				all.checked = false;
			else if (count == this.items.length)
				all.checked = true;
			else {
				all.checked = true;
				all.indeterminate = true;
			}
		}
	},
	itemStatusChanged : function(o, s) {
		this.parent(o, s);
		if (s != 'check')
			return;
		var checked = o.getStatus('check');
		o.checkbox.checked = checked;
		this.updateCheckBoxes(o);
	}
});
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
			'class' : 'prompt'
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
		var self = this;
		var p = this.prompt;
		if (typeOf(this.validate) == 'function')
			if (!this.validate()) {
				this.saving = false;
				return;
			}
		new JsonRequest({
			url : url ? url : this.openps['save_url'],
			onError : function(c) {
				if (p)
					p.set('text', '保存失败:' + c.errMsg);
				self.saving = false;
			},
			onSuccess : function(r) {
				if (p)
					p.set('text', '');
				if (self.fireEvent('saveComplete', r))
					self.show(false);
			}
		}).send(this.content.toQueryString());
		if (p)
			p.set('text', '正在保存...');
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
