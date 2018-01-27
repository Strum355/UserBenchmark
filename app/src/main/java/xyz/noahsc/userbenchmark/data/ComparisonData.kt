package xyz.noahsc.userbenchmark.data

class ComparisonData {

    companion object {
        private var toCompare1: Hardware? = null
        private var toCompare2: Hardware? = null

        fun getCompareFirst(): Hardware? {
            return toCompare1
        }

        fun getCompareSecond(): Hardware? {
            return toCompare2
        }

        fun setCompareFirst(h: Hardware?) {
            toCompare1 = h
        }

        fun setCompareSecond(h: Hardware?) {
            toCompare2 = h
        }

        fun reset() {
            toCompare1 = null
            toCompare2 = null
        }
    }
}

