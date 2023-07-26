import _locale
import json

_locale._getdefaultlocale = (lambda *args: ['en_US', 'utf8'])


def parse(prev, _template, _value):
    if type(_template) is dict:
        for _item in _template:
            parse((prev + ('' if prev == '' else '.') + _item), _template[_item],
                  None if _value is None or _item not in _value else _value[_item])
    elif type(_template) is list and len(_template) > 0:
        parse(prev, _template[0], None if _value is None or len(_value) == 0 else _value[0])
    elif _value is None:
        return print(prev + ':None ERROR')
    elif type(_template) != type(_value):
        return print(prev + ':TYPE ERROR')
    else:
        print(prev + ':OK')


file = open('data.json')
value = json.loads(file.read())
file.close()
file = open('template.json')
template = json.loads(file.read())
file.close()
parse('', template, value)
