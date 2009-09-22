import io/Reader

fopen: extern func(filename: Char*, mode: Char*) -> FILE*
fread: extern func(ptr: Pointer, size: SizeT, count: SizeT, stream: FILE*) -> SizeT
feof: extern func(stream: FILE*) -> Int
fseek: extern func(stream: FILE*, offset: Long, origin: Int)
SEEK_CUR: extern func()
SEEK_SET: extern func()
SEEK_END: extern func()
ftell: extern func(stream: FILE*) -> Long
 
FileReader: class extends Reader {

	file: FILE*
	
	init: func(fileName: String) {
		file = fopen(fileName, "r");
		if (!file) 
			Exception new(This, "File not found: " + fileName) throw()
	}

	read: func(chars: String, offset: Int, count: Int) {
		fread(chars + offset, 1, count, file);
	}
	
	readChar: func() -> Char {
		value: Char
		fread(value&, 1, 1, file)
		return value
	}
	
	hasNext: func() -> Bool {
		return !feof(file)
	}
	
	rewind: func(offset: Int) {
		fseek(file, -offset, SEEK_CUR);
	}
	
	mark: func() -> Long { 
		marker = ftell(file);
		return marker;
	}
	
	reset: func(marker: Long) {
		fseek(file, marker, SEEK_SET);
	}
}

main: func() {
	fr := FileReader new("/etc/hosts") 
	
	while (fr hasNext())
		fr readChar() print()
	
}