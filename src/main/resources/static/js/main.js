// Main JavaScript for Employee Management System

document.addEventListener('DOMContentLoaded', function() {
    console.log('Employee Management System loaded');
    
    // Initialize all components
    initializeComponents();
    initializeFormValidation();
    initializeTooltips();
    initializeAnimations();
});

// Initialize all components
function initializeComponents() {
    // Auto-hide alerts after 5 seconds
    autoHideAlerts();
    
    // Initialize search functionality
    initializeSearch();
    
    // Initialize form enhancements
    initializeFormEnhancements();
    
    // Initialize table enhancements
    initializeTableEnhancements();
}

// Auto-hide success/error alerts
function autoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    });
}

// Enhanced search functionality
function initializeSearch() {
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        // Add search icon animation
        searchInput.addEventListener('focus', function() {
            this.parentElement.classList.add('search-focused');
        });
        
        searchInput.addEventListener('blur', function() {
            this.parentElement.classList.remove('search-focused');
        });
        
        // Real-time search suggestions (if needed)
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();
            
            if (query.length > 2) {
                searchTimeout = setTimeout(() => {
                    // Implement real-time search here if needed
                    console.log('Searching for:', query);
                }, 300);
            }
        });
    }
}

// Form validation and enhancements
function initializeFormValidation() {
    // Bootstrap form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                
                // Focus on first invalid field
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                }
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Real-time validation
    const inputs = document.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('is-invalid')) {
                validateField(this);
            }
        });
    });
}

// Validate individual field
function validateField(field) {
    const isValid = field.checkValidity();
    field.classList.remove('is-valid', 'is-invalid');
    field.classList.add(isValid ? 'is-valid' : 'is-invalid');
    
    // Custom validation messages
    const feedback = field.parentElement.querySelector('.invalid-feedback');
    if (feedback && !isValid) {
        if (field.validity.valueMissing) {
            feedback.textContent = `${field.labels[0]?.textContent || 'This field'} is required.`;
        } else if (field.validity.typeMismatch) {
            feedback.textContent = 'Please enter a valid value.';
        } else if (field.validity.patternMismatch) {
            feedback.textContent = 'Please match the required format.';
        }
    }
}

// Form enhancements
function initializeFormEnhancements() {
    // Salary input formatting
    const salaryInputs = document.querySelectorAll('input[type="number"][id*="salary"]');
    salaryInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Remove non-numeric characters except decimal point
            this.value = this.value.replace(/[^0-9.]/g, '');
            
            // Prevent multiple decimal points
            const parts = this.value.split('.');
            if (parts.length > 2) {
                this.value = parts[0] + '.' + parts.slice(1).join('');
            }
        });
        
        input.addEventListener('blur', function() {
            // Format number with commas for display
            if (this.value) {
                const num = parseFloat(this.value);
                if (!isNaN(num)) {
                    // Store original value for form submission
                    this.dataset.originalValue = this.value;
                    // Display formatted value
                    this.value = num.toLocaleString('en-US', {
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 2
                    });
                }
            }
        });
        
        input.addEventListener('focus', function() {
            // Restore original value for editing
            if (this.dataset.originalValue) {
                this.value = this.dataset.originalValue;
            }
        });
    });
    
    // Phone number formatting
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Basic phone formatting (XXX) XXX-XXXX
            let value = this.value.replace(/\D/g, '');
            if (value.length >= 6) {
                value = `(${value.slice(0,3)}) ${value.slice(3,6)}-${value.slice(6,10)}`;
            } else if (value.length >= 3) {
                value = `(${value.slice(0,3)}) ${value.slice(3)}`;
            }
            this.value = value;
        });
    });
}

// Table enhancements
function initializeTableEnhancements() {
    // Add loading state for table actions
    const actionButtons = document.querySelectorAll('.table .btn');
    actionButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.type === 'submit' || this.href) {
                this.innerHTML = '<span class="loading"></span> Loading...';
                this.disabled = true;
            }
        });
    });
    
    // Table row selection (if needed)
    const tableRows = document.querySelectorAll('.table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('click', function(e) {
            // Don't select row if clicking on action buttons
            if (!e.target.closest('.btn')) {
                row.classList.toggle('table-active');
            }
        });
    });
}

// Initialize tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Initialize animations
function initializeAnimations() {
    // Fade in cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

// Utility functions

// Show loading state
function showLoading(element, text = 'Loading...') {
    const originalContent = element.innerHTML;
    element.innerHTML = `<span class="loading"></span> ${text}`;
    element.disabled = true;
    element.dataset.originalContent = originalContent;
}

// Hide loading state
function hideLoading(element) {
    if (element.dataset.originalContent) {
        element.innerHTML = element.dataset.originalContent;
        element.disabled = false;
        delete element.dataset.originalContent;
    }
}

// Show notification
function showNotification(message, type = 'info', duration = 5000) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    // Auto-remove after duration
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, duration);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format date
function formatDate(date) {
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    }).format(new Date(date));
}

// API helper functions
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    try {
        const response = await fetch(url, { ...defaultOptions, ...options });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API request failed:', error);
        showNotification('An error occurred. Please try again.', 'danger');
        throw error;
    }
}

// Export functions for use in other scripts
window.EmployeeManagement = {
    showLoading,
    hideLoading,
    showNotification,
    formatCurrency,
    formatDate,
    apiRequest
};