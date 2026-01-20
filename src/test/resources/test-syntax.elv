# Test file for Elvish syntax highlighting verification
# Comments should appear in gray (#676E95)

# Keywords (purple #C792EA)
# Control keywords
if $true {
    echo "condition true"
} elif $false {
    echo "condition false"
} else {
    echo "fallback"
}

while $true {
    break
    continue
}

for x [(range 10)] {
    echo $x
}

try {
    fail "error"
} catch e {
    echo $e
} finally {
    echo "cleanup"
}

# Function definition keyword
fn greet {|name|
    echo "Hello, "$name
    return
}

# Other keywords
var myvar = "value"
set myvar = "new value"
tmp myvar = "temporary"
del myvar

use str
pragma unknown-command = external

# Constants (should appear in constant color)
var t = $true
var f = $false
var n = $nil
var ok = $ok
nop

# Numbers (orange #F78C6C)
var integer = 42
var negative = -17
var float = 3.14159
var scientific = 1.5e10
var hex = 0xDEADBEEF
var octal = 0o755
var binary = 0b101010
var rational = 22/7
var infinity = Inf
var neginfinity = -Inf
var notanumber = NaN

# Strings (green #C3E88D)
var single = 'single quoted string with ''escaped'' quote'
var double = "double quoted with\nnewline and\ttab"
var escape = "escapes: \\ \" \x41 \u0041 \U00000041"

# Variables (yellow #FFCB6B)
var simple = $PATH
var exploded = $@args
var namespaced = $str:join
echo "interpolated: $simple"

# Built-in functions (blue #82AAFF)
put "value"
echo "hello"
each {|x| echo $x }
peach {|x| process $x }
all
range 1 10
count [a b c]
keys [&a=1 &b=2]
has-key [&a=1] a
has-value [a b c] b
from-json '{"key": "value"}'
to-json [&key=value]
slurp
print "no newline"
printf "formatted %s\n" "string"
repr [1 2 3]
pprint [&nested=[&map=true]]
kind-of "string"
bool 1
num "42"
exact-num 3.14
inexact-num 3
base 16 255
compare a b
order [3 1 2]
eq $a $b
is $a $b
not $false
not-eq $a $b
assoc [&a=1] b 2
dissoc [&a=1 &b=2] a
conj [a b] c
take 5 [(range 100)]
drop 5 [(range 100)]
compact [a "" b $nil c]
keep-if {|x| > $x 0 } [(range -5 5)]
search-external elvish
has-external git
resolve echo
external cat
exec /bin/ls
cd /tmp
get-env HOME
set-env MYVAR value
has-env PATH
unset-env MYVAR
exit 0
sleep 1
time { nop }
benchmark { nop }
fail "error message"
constantly [value]
call {|| echo "called" } []
eval "echo evaluated"
read-line
read-upto "\n"
read-bytes 1024
styled "text" red bold
styled-segment "segment" &fg=red
wcswidth "hello"
show [1 2 3]

# Operators
var cmp1 = (== 1 1)
var cmp2 = (!= 1 2)
var cmp3 = (< 1 2)
var cmp4 = (> 2 1)
var cmp5 = (<= 1 1)
var cmp6 = (>= 1 1)
var strcmp = (==s "a" "a")

var add = (+ 1 2)
var sub = (- 5 3)
var mul = (* 2 3)
var div = (/ 10 2)
var mod = (% 7 3)

# Pipe operator
echo "hello" | tr a-z A-Z

# Range operators
range 1..10
range 1..=10

# Redirection
echo "out" > /tmp/out.txt
echo "append" >> /tmp/out.txt
cat < /tmp/out.txt

# Logical operators (keywords)
and $true $true
or $false $true
coalesce $nil "default"

# Punctuation and structure
var list = [a b c]
var map = [&key=value &num=42]
var nested = [&list=[1 2 3] &map=[&a=1]]
var group = (+ 1 2)
echo one; echo two
{ echo "block" }

# Lambda with arguments
var double = {|x| * $x 2 }
var filtered = (keep-if {|x| > $x 5 } [(range 10)])

# End of test file
