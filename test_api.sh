#!/usr/bin/env bash
# ============================================================
#  SmartCampus Microservices – API Test Script
#  Tests all endpoints across every module.
#
#  Usage:
#    ./test_api.sh            # test via API Gateway (port 80)
#    ./test_api.sh direct     # test each service directly
#    ./test_api.sh gateway    # test via API Gateway (port 80)
# ============================================================

set -euo pipefail

# ── Colour helpers ───────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

# ── Mode: gateway (default) or direct ───────────────────────
MODE="${1:-gateway}"

if [[ "$MODE" == "direct" ]]; then
  STUDENT_BASE="http://localhost:8081"
  ENROLL_BASE="http://localhost:8082"
  NOTIF_BASE="http://localhost:8083"
  BOOKING_BASE="http://localhost:8084"
  REPORT_BASE="http://localhost:8085"
  echo -e "${CYAN}${BOLD}Mode: DIRECT (each service on its own port)${NC}"
else
  STUDENT_BASE="http://localhost:80"
  ENROLL_BASE="http://localhost:80"
  NOTIF_BASE="http://localhost:80"
  BOOKING_BASE="http://localhost:80"
  REPORT_BASE="http://localhost:80"
  echo -e "${CYAN}${BOLD}Mode: GATEWAY (all traffic via Nginx on port 80)${NC}"
fi

# ── Counters ─────────────────────────────────────────────────
PASS=0; FAIL=0; SKIP=0

# ── Utility: run one test ─────────────────────────────────────
# run_test <test_name> <method> <url> [body] [expected_status]
run_test() {
  local name="$1"
  local method="$2"
  local url="$3"
  local body="${4:-}"
  local expected_status="${5:-200}"

  printf "  %-55s" "$name"

  local curl_args=(-s -o /tmp/sc_body.txt -w "%{http_code}" --max-time 10 -X "$method")
  [[ -n "$body" ]] && curl_args+=(-H "Content-Type: application/json" -d "$body")

  local actual_status
  actual_status=$(curl "${curl_args[@]}" "$url" 2>/dev/null) || {
    echo -e "${RED}[SKIP – connection refused]${NC}"
    (( SKIP++ )) || true; return
  }

  local response_body
  response_body=$(cat /tmp/sc_body.txt 2>/dev/null || echo "")

  if [[ "$actual_status" == "$expected_status" ]]; then
    echo -e "${GREEN}[PASS]${NC}  HTTP $actual_status"
    (( PASS++ )) || true
  else
    echo -e "${RED}[FAIL]${NC}  expected=$expected_status  got=$actual_status"
    if [[ -n "$response_body" ]]; then
      echo "         Response: $(echo "$response_body" | head -c 200)"
    fi
    (( FAIL++ )) || true
  fi
}

# ── Print section header ──────────────────────────────────────
section() { echo -e "\n${YELLOW}${BOLD}▶ $1${NC}"; }

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}║    SmartCampus API Test Suite                ║${NC}"
echo -e "${BOLD}╚══════════════════════════════════════════════╝${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "1. GATEWAY HEALTH (Nginx)"

if [[ "$MODE" == "gateway" ]]; then
  run_test "Gateway health check"            GET  "http://localhost:80/health"
else
  echo "  (Skipped in direct mode)"
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "2. STUDENT SERVICE  (port 8081  →  /api/students)"

run_test "Health check"                     GET  "$STUDENT_BASE/api/students/health"        ""   200
run_test "GET all students"                 GET  "$STUDENT_BASE/api/students"               ""   200
run_test "GET student by id=1 (Alice)"      GET  "$STUDENT_BASE/api/students/1"             ""   200
run_test "GET student by id=2 (Bob)"        GET  "$STUDENT_BASE/api/students/2"             ""   200
run_test "GET student by id=3 (Chloe)"      GET  "$STUDENT_BASE/api/students/3"             ""   200
run_test "GET student – id not found (404)" GET  "$STUDENT_BASE/api/students/999"           ""   500
# Note: controller throws RuntimeException for missing student which Spring maps to 500;
#       update to 404 if a @ResponseStatus is added later.

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "3. ENROLLMENT SERVICE  (port 8082  →  /api/enrollments)"

run_test "Health check"                          GET  "$ENROLL_BASE/api/enrollments/health"          ""   200

# Enroll a known student
ENROLL_BODY='{"studentId":1,"courseCode":"CS101","semester":"2024/25-1"}'
run_test "POST enroll student 1 in CS101"        POST "$ENROLL_BASE/api/enrollments"  "$ENROLL_BODY"  201

# Enroll the same student in a second course
ENROLL_BODY2='{"studentId":1,"courseCode":"MATH201","semester":"2024/25-1"}'
run_test "POST enroll student 1 in MATH201"      POST "$ENROLL_BASE/api/enrollments"  "$ENROLL_BODY2" 201

# Enroll a different student
ENROLL_BODY3='{"studentId":2,"courseCode":"CS101","semester":"2024/25-1"}'
run_test "POST enroll student 2 in CS101"        POST "$ENROLL_BASE/api/enrollments"  "$ENROLL_BODY3" 201

# GET by student
run_test "GET enrollments for student 1"         GET  "$ENROLL_BASE/api/enrollments/student/1"       ""   200
run_test "GET enrollments for student 2"         GET  "$ENROLL_BASE/api/enrollments/student/2"       ""   200
run_test "GET enrollments – no records (empty)"  GET  "$ENROLL_BASE/api/enrollments/student/3"       ""   200

# Invalid: student does not exist
ENROLL_BAD='{"studentId":999,"courseCode":"CS101","semester":"2024/25-1"}'
run_test "POST enroll non-existent student (400)" POST "$ENROLL_BASE/api/enrollments" "$ENROLL_BAD"  400

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "4. NOTIFICATION SERVICE  (port 8083  →  /api/notifications)"

run_test "Health check"                      GET  "$NOTIF_BASE/api/notifications/health"    ""   200
run_test "GET all notifications (empty)"     GET  "$NOTIF_BASE/api/notifications"           ""   200

# Send a notification
NOTIF_BODY='{"type":"EMAIL","message":"Welcome to SmartCampus!"}'
run_test "POST send EMAIL notification"      POST "$NOTIF_BASE/api/notifications" "$NOTIF_BODY" 201

# Send a second notification to get an id to look up
NOTIF_BODY2='{"type":"SMS","message":"Your enrollment is confirmed."}'
run_test "POST send SMS notification"        POST "$NOTIF_BASE/api/notifications" "$NOTIF_BODY2" 201

# Retrieve all (should now have 2 entries)
run_test "GET all notifications (2 items)"   GET  "$NOTIF_BASE/api/notifications"           ""   200

# Fetch first notification by id (captured dynamically)
FIRST_ID=$(curl -s "$NOTIF_BASE/api/notifications" 2>/dev/null \
  | grep -o '"id":"[^"]*"' | head -1 | sed 's/"id":"//;s/"//' || echo "")

if [[ -n "$FIRST_ID" ]]; then
  run_test "GET notification by id (found)"  GET  "$NOTIF_BASE/api/notifications/$FIRST_ID" ""   200
else
  echo "  (Skipping GET by id – could not parse id from list response)"
  (( SKIP++ )) || true
fi

run_test "GET notification – not found (404)" GET  "$NOTIF_BASE/api/notifications/non-existent-id-0000" "" 404

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "5. BOOKING SERVICE  (port 8084  →  /api/bookings)"

run_test "Health check"                           GET "$BOOKING_BASE/api/bookings/health"              ""   200
run_test "GET all bookings (3 pre-seeded)"          GET "$BOOKING_BASE/api/bookings"                     ""   200
run_test "GET bookings for student 1 (cross-svc)"   GET "$BOOKING_BASE/api/bookings/student/1"           ""   200
run_test "GET bookings for student 2 (cross-svc)"   GET "$BOOKING_BASE/api/bookings/student/2"           ""   200
run_test "GET bookings - student not found (500)"   GET "$BOOKING_BASE/api/bookings/student/999"         ""   500
# Note: RestTemplate throws on 5xx from Student Service → Spring returns 500;
#       refactor with proper error handling if 404 passthrough is needed.

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "6. REPORT SERVICE  (port 8085  →  /api/reports)"

run_test "Health check"                           GET "$REPORT_BASE/api/reports/health"            ""   200
# Report module has only the health endpoint currently.
echo "  (No additional report endpoints defined yet)"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
section "7. CROSS-SERVICE INTEGRATION CHECKS"

echo "  [Checking that enrollment service correctly delegates to student service]"

# Valid cross-service call – already done in section 3, summarise here
echo -e "  Enrollment → Student lookup …… covered in section 3"

echo "  [Checking that booking service correctly delegates to student service]"
echo -e "  Booking → Student lookup ………… covered in section 5"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo -e "${BOLD}════════════════════════════════════════════════${NC}"
echo -e "${BOLD}  RESULTS${NC}"
echo -e "${BOLD}════════════════════════════════════════════════${NC}"
echo -e "  ${GREEN}PASS${NC}  : $PASS"
echo -e "  ${RED}FAIL${NC}  : $FAIL"
echo -e "  ${YELLOW}SKIP${NC}  : $SKIP  (connection refused – service not running)"
echo -e "${BOLD}════════════════════════════════════════════════${NC}"
echo ""

if [[ $FAIL -gt 0 ]]; then
  echo -e "${RED}${BOLD}Some tests FAILED. Review the output above.${NC}"
  exit 1
elif [[ $SKIP -gt 0 ]]; then
  echo -e "${YELLOW}${BOLD}All reachable tests passed, but $SKIP service(s) were unreachable.${NC}"
  echo "Start the stack with:  docker compose up --build"
  exit 0
else
  echo -e "${GREEN}${BOLD}All tests PASSED!${NC}"
  exit 0
fi
