version(linux) {
    include unistd | (__USE_BSD), sys/time | (__USE_BSD), time | (__USE_BSD)
}

version(!linux) {
    include unistd, sys/time
}

usleep: extern func (UInt)

TimeT: cover from time_t

TMStruct: cover from struct tm {
    tm_sec, tm_min, tm_hour, tm_mday, tm_mon, tm_year, tm_wday, tm_yday, tm_isdst : extern Int
}

time: extern proto func (TimeT*) -> TimeT
localtime: extern func (TimeT*) -> TMStruct*

TimeVal: cover from struct timeval {
	tv_sec: extern TimeT
	tv_usec: extern SUSecondsT
}

TimeZone: cover from struct timezone
SUSecondsT: cover from suseconds_t extends Int

gettimeofday: extern func (TimeVal*, TimeZone*) -> Int

Time: class {
	
	microtime: static func -> LLong {
		return microsec() as LLong + (sec() as LLong) * 1_000_000
	}
	
	microsec: static func -> SUSecondsT {
		tv : TimeVal
		gettimeofday(tv&, null)
		return tv tv_usec
	}
	
	sec: static func -> UInt {
		tt := time(null)
		val := localtime(tt&)
		return val@ tm_sec
	}
	
	min: static func -> UInt {
		tt := time(null)
		val := localtime(tt&)
		return val@ tm_min
	}
	
	hour: static func -> UInt {
		tt := time(null)
		val := localtime(tt&)
		return val@ tm_hour
	}
	
	sleepSec: static func (duration: Float) {
		usleep(duration * 1_000_000)
	}

	sleepMilli: static func (duration: UInt) {
		usleep(duration * 1_000)
	}

	sleepMicro: static func (duration: UInt) {
		usleep(duration)
	}

}
