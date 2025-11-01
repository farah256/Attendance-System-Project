// src/components/Layout.jsx
import React from "react";
import { Link } from "react-router-dom";

export default function Layout({ children }) {
    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <aside className="w-64 bg-white p-4 border-r">
                <h2 className="text-xl font-bold mb-6">Menu</h2>
                <nav className="space-y-3">
                    <Link className="block text-sm font-medium hover:text-red-500" to="/dashboard">📊 Tableau de bord</Link>
                    <Link className="block text-sm" to="/classes">🏫 Classes</Link>
                    <Link className="block text-sm" to="/etudiants">👨‍🎓 Étudiants</Link>
                    <Link className="block text-sm" to="/absences">📅 Absences</Link>
                    <Link className="block text-sm text-red-600 font-semibold" to="/alertes">⚠️ Alertes</Link>
                    <Link className="block text-sm" to="/stats">📈 Statistiques</Link>
                    <Link className="block text-sm" to="/notes">📝 Notes</Link>
                </nav>
            </aside>

            {/* Main content */}
            <div className="flex-1 overflow-auto">
                {/* Header */}
                <header className="flex items-center justify-between px-6 py-4 bg-white shadow">
                    <h1 className="text-xl font-bold">Gestion des Absences</h1>
                    <div className="flex items-center space-x-4">
                        <span className="bg-black text-white rounded-full px-3 py-1">GA</span>

                        <Link className="block text-sm text-red-600 font-semibold" to="/alertes">🔔</Link>

                    </div>
                </header>

                {/* Page content */}
                <main className="p-6">
                    {children}
                </main>
            </div>
        </div>
    );
}
