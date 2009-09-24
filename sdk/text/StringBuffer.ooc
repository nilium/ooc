StringBuffer: class {
	
	size: SizeT
	capacity: SizeT
	data: String
	
	init: func {
		this(128)
	}
	
	init: func ~withCapa (=capacity) {
		data = gc_malloc(capacity)
		size = 0
	}
	
	append: func ~str (str: String) {
		length := str length()
		checkLength(size + length)
		memcpy(data + size, str, length)
		size += length
	}
	
	append: func ~chr (chr: Char) {
		checkLength(size + 1)
		data[size] = chr
		size += 1
	}
	
	checkLength: func (min: SizeT) {
		if(min >= capacity) {
			newCapa := min * 1.2 + 10
			tmp := gc_realloc(data, newCapa)
			if(!tmp) {
				Exception new(This, "Couldn't allocate enough memory for StringBuffer to grow to capacity "+newCapa) throw()
			}
			data = tmp
			capacity = newCapa
		}
	}
	
	toString: func -> String {
		data[capacity] = '\0'
		return data // ugly hack. or is it?
	}
	
}