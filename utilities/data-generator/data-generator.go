package main

import (
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"time"
)

// Tag status constants
const (
	Valid = iota + 1
	LowBalance
	Invalid
	Deprecated
)

// Generates a header for the tag status file.
func generateHeader(agencyID string, recordCount int, statusCounts map[int]int) string {
	currentTime := time.Now()
	fileDate := currentTime.Format("20060102")
	fileTime := currentTime.Format("150405")
	header := fmt.Sprintf("ITAG%s%s%s%08d%08d%08d%08d\n",
		agencyID,
		fileDate,
		fileTime,
		recordCount,
		statusCounts[Valid],
		statusCounts[LowBalance],
		statusCounts[Invalid],
	)

	return header
}

// Generates a detail record for the tag status file.
func generateDetail(tagAgencyID string, serialNumber int, status int, acctInfo string) string {
	detail := fmt.Sprintf("%s%08d%1d%s\n",
		tagAgencyID,
		serialNumber,
		status,
		acctInfo,
	)

	return detail
}

func main() {
	// You can change these values for the agency ID and record count as needed.
	const fromAgencyID = "008"
	const numRecords = 10000

	statusCounts := map[int]int{
		Valid:      0,
		LowBalance: 0,
		Invalid:    0,
		Deprecated: 0,
	}

	// Seed random number generator
	rand.Seed(time.Now().UnixNano())

	fileName := fmt.Sprintf("%s_%s.ITAG", fromAgencyID, time.Now().Format("20060102150405"))
	file, err := os.Create(fileName)
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	// Generate header
	header := generateHeader(fromAgencyID, numRecords, statusCounts)
	_, err = file.WriteString(header)
	if err != nil {
		fmt.Println("Error writing to file:", err)
		return
	}

	// Generate detail records
	for i := 1; i <= numRecords; i++ {
		tagStatus := rand.Intn(4) + 1
		statusCounts[tagStatus]++
		detail := generateDetail(fromAgencyID, i, tagStatus, generateTagAcctInfo())
		_, err := file.WriteString(detail)
		if err != nil {
			fmt.Println("Error writing to file:", err)
			return
		}
	}

	fmt.Println("Tag Status File generated:", fileName)
}

// randomBit generates a single random bit as a character '0' or '1'.
func randomBit() byte {
	if rand.Intn(2) == 0 {
		return '0'
	}
	return '1'
}

// generateTagAcctInfo creates a random TAG_ACCT_INFO value according to the provided specs.
func generateTagAcctInfo() string {
	// Initialize a slice of bytes to store 24 bits.
	bits := make([]byte, 24)

	// Bit 1 (rightmost bit): E-ZPass Plus - Parking status (0 or 1).
	bits[23] = randomBit() // Indexing from the left, this is the rightmost bit.

	// Bits 2-14: Randomly set for various discount plans.
	for i := 22; i >= 10; i-- {
		bits[i] = randomBit()
	}

	// Bits 15-23 are reserved and must be set to '0'.
	for i := 9; i >= 1; i-- {
		bits[i] = '0'
	}

	// Bit 24 (leftmost bit): E-ZPass Plus - Non-Parking status, currently set to '0'.
	bits[0] = '0' // Indexing from the left, this is the leftmost bit.

	// Convert the binary string to an integer.
	binaryStr := string(bits)
	num, err := strconv.ParseUint(binaryStr, 2, 64)
	if err != nil {
		fmt.Println("Error converting binary to integer:", err)
		return ""
	}

	// Format the integer as a hexadecimal string.
	return fmt.Sprintf("%06X", num)
}
